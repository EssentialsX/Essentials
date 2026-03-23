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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rate-limited webhook dispatcher that sends at most one message per drain cycle
 * and backs off exponentially when Discord returns 429 (rate limited).
 * <p>
 * Discord webhook rate limit: ~5 requests per 2 seconds per webhook.
 * We send 1 message every 500ms (= 4/2s), staying under the limit with headroom.
 * On 429, we pause all sends and back off exponentially.
 */
public class WebhookDispatcher {
    private static final Logger logger = EssentialsDiscord.getWrappedLogger();

    private static final long DRAIN_INTERVAL_MS = 500;
    private static final int DEFAULT_QUEUE_CAPACITY = 30;
    private static final long INITIAL_BACKOFF_MS = 4000;
    private static final long MAX_BACKOFF_MS = 30000;

    private final WrappedWebhookClient client;
    private final BlockingQueue<PendingMessage> queue;
    private final ScheduledExecutorService scheduler;
    private final ScheduledFuture<?> drainTask;

    private final AtomicInteger totalDropped = new AtomicInteger(0);
    private final AtomicInteger total429s = new AtomicInteger(0);

    private volatile long backoffUntil = 0;
    private volatile long currentBackoffMs = INITIAL_BACKOFF_MS;
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

        final long now = System.currentTimeMillis();
        if (now < backoffUntil) {
            return;
        }

        final PendingMessage pending = queue.poll();
        if (pending == null) {
            return;
        }

        try {
            client.send(pending.message).whenComplete((result, error) -> {
                if (error != null) {
                    final String errorMsg = error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName();
                    if (errorMsg.contains("429") || errorMsg.contains("rate limit") || errorMsg.toLowerCase().contains("ratelimit")) {
                        final int count429 = total429s.incrementAndGet();
                        backoffUntil = System.currentTimeMillis() + currentBackoffMs;
                        logger.warning("Webhook rate limited (429). Backing off " + currentBackoffMs + "ms. Queue: " + queue.size() + ", 429 count: " + count429 + ", dropped: " + totalDropped.get());
                        currentBackoffMs = Math.min(currentBackoffMs * 2, MAX_BACKOFF_MS);
                    } else {
                        logger.log(Level.WARNING, "Webhook send failed", error);
                    }
                    pending.future.completeExceptionally(error);
                } else {
                    currentBackoffMs = INITIAL_BACKOFF_MS;
                    pending.future.complete(result);
                }
            });
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error dispatching webhook message", e);
            pending.future.completeExceptionally(e);
        }
    }

    public void abandonRequests() {
        PendingMessage pending;
        int count = 0;
        while ((pending = queue.poll()) != null) {
            pending.future.complete(null);
            count++;
        }
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
