package net.essentialsx.discord.util;

import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookMessage;
import net.essentialsx.discord.EssentialsDiscord;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebhookDispatcher {
    private static final Logger logger = EssentialsDiscord.getWrappedLogger();

    private static final int MAX_TOKENS = 5;
    private static final long REFILL_PERIOD_MS = 2000;
    private static final long DRAIN_INTERVAL_MS = 400;
    private static final int DEFAULT_QUEUE_CAPACITY = 50;

    private final WrappedWebhookClient client;
    private final BlockingQueue<PendingMessage> queue;
    private final ScheduledExecutorService scheduler;
    private final ScheduledFuture<?> drainTask;

    private final AtomicInteger tokens = new AtomicInteger(MAX_TOKENS);
    private final AtomicLong lastRefillTime = new AtomicLong(System.currentTimeMillis());

    private final AtomicInteger suppressedCount = new AtomicInteger(0);
    private final AtomicInteger totalDropped = new AtomicInteger(0);
    private final AtomicInteger total429s = new AtomicInteger(0);

    private volatile boolean shutdown = false;

    public WebhookDispatcher(WrappedWebhookClient client) {
        this(client, DEFAULT_QUEUE_CAPACITY);
    }

    public WebhookDispatcher(WrappedWebhookClient client, int queueCapacity) {
        this.client = client;
        this.queue = new LinkedBlockingQueue<>(queueCapacity);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread t = new Thread(r, "EssX-WebhookDispatcher-" + client.getId());
            t.setDaemon(true);
            return t;
        });
        this.drainTask = scheduler.scheduleAtFixedRate(this::drain, DRAIN_INTERVAL_MS, DRAIN_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<ReadonlyMessage> send(WebhookMessage message) {
        if (shutdown) {
            return CompletableFuture.completedFuture(null);
        }

        final CompletableFuture<ReadonlyMessage> future = new CompletableFuture<>();
        final PendingMessage pending = new PendingMessage(message, future);

        if (!queue.offer(pending)) {
            final PendingMessage dropped = queue.poll();
            if (dropped != null) {
                dropped.future.complete(null);
                suppressedCount.incrementAndGet();
                totalDropped.incrementAndGet();
            }

            if (!queue.offer(pending)) {
                future.complete(null);
                totalDropped.incrementAndGet();
            }
        }

        return future;
    }

    private void drain() {
        if (shutdown) {
            return;
        }

        refillTokens();

        while (!queue.isEmpty()) {
            if (tokens.get() <= 0) {
                break;
            }

            final PendingMessage pending = queue.poll();
            if (pending == null) {
                break;
            }

            final int remaining = tokens.decrementAndGet();
            if (remaining < 0) {
                tokens.incrementAndGet();
                if (!queue.offer(pending)) {
                    pending.future.complete(null);
                    suppressedCount.incrementAndGet();
                }
                break;
            }

            final int suppressed = suppressedCount.getAndSet(0);
            if (suppressed > 0) {
                logger.info("Webhook dispatcher: " + suppressed + " message(s) dropped due to rate limit backpressure. " + "(Total dropped: " + totalDropped.get() + ", Total 429s: " + total429s.get() + ")");
            }

            try {
                client.send(pending.message).whenComplete((result, error) -> {
                    if (error != null) {
                        final String errorMsg = error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName();
                        if (errorMsg.contains("429") || errorMsg.contains("rate limit")) {
                            total429s.incrementAndGet();
                            logger.warning("Webhook rate limited (429). Queue depth: " + queue.size() + ", total 429s: " + total429s.get() + ", total dropped: " + totalDropped.get());
                        } else {
                            logger.log(Level.WARNING, "Webhook send failed", error);
                        }
                        pending.future.completeExceptionally(error);
                    } else {
                        pending.future.complete(result);
                    }
                });
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error dispatching webhook message", e);
                pending.future.completeExceptionally(e);
            }
        }
    }

    private void refillTokens() {
        final long now = System.currentTimeMillis();
        final long last = lastRefillTime.get();

        if (now - last >= REFILL_PERIOD_MS) {
            if (lastRefillTime.compareAndSet(last, now)) {
                tokens.set(MAX_TOKENS);
            }
        }
    }

    public void abandonRequests() {
        PendingMessage pending;
        int count = 0;
        while ((pending = queue.poll()) != null) {
            pending.future.complete(null);
            count++;
        }
        suppressedCount.set(0);
        client.abandonRequests();
        if (count > 0) {
            logger.info("WebhookDispatcher: Abandoned " + count + " pending message(s).");
        }
    }

    public void close() {
        shutdown = true;
        drainTask.cancel(false);
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }

        // Complete any remaining futures so callers are not left hanging
        PendingMessage pending;
        while ((pending = queue.poll()) != null) {
            pending.future.complete(null);
        }

        client.close();
    }

    public boolean isShutdown() {
        return shutdown || client.isShutdown();
    }

    public WrappedWebhookClient getClient() {
        return client;
    }

    public int getQueueSize() {
        return queue.size();
    }

    public int getTotalDropped() {
        return totalDropped.get();
    }

    public int getTotal429s() {
        return total429s.get();
    }

    private static class PendingMessage {
        final WebhookMessage message;
        final CompletableFuture<ReadonlyMessage> future;

        PendingMessage(WebhookMessage message, CompletableFuture<ReadonlyMessage> future) {
            this.message = message;
            this.future = future;
        }
    }
}
