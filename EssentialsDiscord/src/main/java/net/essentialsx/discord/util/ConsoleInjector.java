package net.essentialsx.discord.util;

import com.earth2me.essentials.utils.FormatUtil;
import com.google.common.base.Splitter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.ess3.provider.SchedulingProvider;
import net.essentialsx.discord.EssentialsDiscord;
import net.essentialsx.discord.JDADiscordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;

@Plugin(name = "EssentialsX-ConsoleInjector", category = "Core", elementType = "appender", printObject = true)
public class ConsoleInjector extends AbstractAppender {
    private final static java.util.logging.Logger logger = EssentialsDiscord.getWrappedLogger();

    private final static long QUEUE_PROCESS_PERIOD_SECONDS = 2;

    private final JDADiscordService jda;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final SchedulingProvider.EssentialsTask task;
    private boolean removed = false;

    private final AtomicLong lastRateLimitTime = new AtomicLong(0);
    private final AtomicInteger recentRateLimit = new AtomicInteger(0);
    private final AtomicInteger totalBackoffEvents = new AtomicInteger();

    public ConsoleInjector(JDADiscordService jda) {
        super("EssentialsX-ConsoleInjector", null, null, false);
        this.jda = jda;
        ((Logger) LogManager.getRootLogger()).addAppender(this);
        task = jda.getPlugin().getEss().runTaskTimerAsynchronously(() -> {
            // Check to see if we're supposed to be backing off, preform backoff if the case.
            if (recentRateLimit.get() < 0) {
                if (totalBackoffEvents.get() * 20 >= jda.getSettings().getConsoleSkipDelay() * 60) {
                    logger.warning("EssXBackoff: Reached console skip delay, attempt to skip");
                    jda.getConsoleWebhook().abandonRequests();
                    messageQueue.clear();
                    totalBackoffEvents.set(0);
                    recentRateLimit.set(0);
                    lastRateLimitTime.set(0);
                    return;
                }

                final int backoff = recentRateLimit.incrementAndGet();
                if (jda.isDebug()) {
                    logger.warning("EssXBackoff: Webhook backoff in progress, skipping queue processing. Resuming in " + Math.abs(backoff) + " cycles.");
                }
                return;
            }
            final StringBuilder buffer = new StringBuilder();
            String curLine;
            while ((curLine = messageQueue.peek()) != null) {
                if (buffer.length() + curLine.length() > Message.MAX_CONTENT_LENGTH - 2) {
                    sendMessage(buffer.toString());
                    buffer.setLength(0);
                    continue;
                }
                buffer.append("\n").append(messageQueue.poll());
            }
            if (buffer.length() != 0) {
                sendMessage(buffer.toString());
            }
        }, 20, 20 * QUEUE_PROCESS_PERIOD_SECONDS);
    }

    private void sendMessage(String content) {
        jda.getConsoleWebhook().send(jda.getWebhookMessage(content)).exceptionally(e -> {
            logger.severe(tl("discordErrorWebhook"));
            remove();
            return null;
        });
    }

    @Override
    public void append(LogEvent event) {
        if (event.getLevel().intLevel() > jda.getSettings().getConsoleLogLevel().intLevel()) {
            return;
        }

        // Ansi strip is for normal colors, normal strip is for 1.16 hex color codes as they are not formatted correctly, adventure strip is for magic color char strip
        String entry = FormatUtil.stripPaper(FormatUtil.stripFormat(FormatUtil.stripAnsi(event.getMessage().getFormattedMessage()))).trim();
        if (entry.isEmpty()) {
            return;
        }

        if (entry.startsWith("EssXBackoff: ")) {
            return;
        }

        if (event.getLoggerName().contains("club.minnced.discord.webhook.WebhookClient") && entry.startsWith("Encountered 429, retrying after ")) {
            if (recentRateLimit.get() >= 0) {
                recentRateLimit.incrementAndGet();
            }

            if (lastRateLimitTime.get() == 0 || System.currentTimeMillis() - lastRateLimitTime.get() > 5000) {
                lastRateLimitTime.set(System.currentTimeMillis());

                // A negative value would mean the timer is current preforming a backoff, don't stop it.
                if (recentRateLimit.get() >= 0) {
                    recentRateLimit.set(0);
                }
            } else if (recentRateLimit.get() >= 2) {
                // Start the webhook backoff, defaulting to 20s, which should reset our bucket.
                if (jda.isDebug()) {
                    totalBackoffEvents.getAndIncrement();
                    logger.warning("EssXBackoff: Beginning Webhook Backoff");
                }
                recentRateLimit.set(-20);
            }
            return;
        }

        if (!jda.getSettings().getConsoleFilters().isEmpty()) {
            for (final Pattern pattern : jda.getSettings().getConsoleFilters()) {
                if (pattern.matcher(entry).find()) {
                    return;
                }
            }
        }

        final String[] loggerNameSplit = event.getLoggerName().split("\\.");
        final String loggerName = loggerNameSplit[loggerNameSplit.length - 1].trim();

        if (!loggerName.isEmpty()) {
            entry = "[" + loggerName + "] " + entry;
        }

        messageQueue.addAll(Splitter.fixedLength(Message.MAX_CONTENT_LENGTH - 50).splitToList(
                MessageUtil.formatMessage(jda.getSettings().getConsoleFormat(),
                        TimeFormat.TIME_LONG.format(Instant.now()),
                        event.getLevel().name(),
                        MessageUtil.sanitizeDiscordMarkdown(entry))));
    }

    public void remove() {
        ((Logger) LogManager.getRootLogger()).removeAppender(this);
        task.cancel();
        messageQueue.clear();
        if (jda.getConsoleWebhook() != null && !jda.getConsoleWebhook().isShutdown()) {
            jda.getConsoleWebhook().close();
        }
        removed = true;
    }

    public boolean isRemoved() {
        return removed;
    }
}
