package net.essentialsx.discord.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.util.ThreadPools;
import net.essentialsx.discord.EssentialsDiscord;
import okhttp3.OkHttpClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WrappedWebhookClient {
    private final static Logger logger = EssentialsDiscord.getWrappedLogger();
    private final WebhookClient webhookClient;
    private final ScheduledExecutorService executorService;

    public WrappedWebhookClient(final long id, final String token, final OkHttpClient client) {
        webhookClient = new WebhookClientBuilder(id, token)
                .setWait(false)
                .setAllowedMentions(AllowedMentions.none())
                .setHttpClient(client)
                .setExecutorService(executorService = ThreadPools.getDefaultPool(id, null, true))
                .build();
    }

    public CompletableFuture<ReadonlyMessage> send(WebhookMessage message) {
        return webhookClient.send(message);
    }

    public boolean isShutdown() {
        return webhookClient.isShutdown();
    }

    public void close() {
        // This call should close the executor service as well
        webhookClient.close();
        if (executorService.isTerminated()) {
            return;
        }
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                throw new InterruptedException("ExecutorService did not terminate in time.");
            }
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Webhook (ID: " + webhookClient.getId() + ") took longer than expected to shutdown, this may have caused some problems.", e);
        }
    }
}
