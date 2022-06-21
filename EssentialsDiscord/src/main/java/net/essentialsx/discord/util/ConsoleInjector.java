package net.essentialsx.discord.util;

import com.earth2me.essentials.utils.FormatUtil;
import com.google.common.base.Splitter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.essentialsx.discord.JDADiscordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;

@Plugin(name = "EssentialsX-ConsoleInjector", category = "Core", elementType = "appender", printObject = true)
public class ConsoleInjector extends AbstractAppender {
    private final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("EssentialsDiscord");

    private final JDADiscordService jda;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final int taskId;

    public ConsoleInjector(JDADiscordService jda) {
        super("EssentialsX-ConsoleInjector", null, null, false);
        this.jda = jda;
        ((Logger) LogManager.getRootLogger()).addAppender(this);
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(jda.getPlugin(), () -> {
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
        }, 20, 40).getTaskId();
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

        if (!jda.getSettings().getConsoleFilters().isEmpty()) {
            for (final Pattern pattern : jda.getSettings().getConsoleFilters()) {
                if (pattern.matcher(entry).find()) {
                    return;
                }
            }
        }

        final String loggerName = event.getLoggerName();
        if (!loggerName.isEmpty() && !loggerName.contains(".")) {
            entry = "[" + event.getLoggerName() + "] " + entry;
        }

        messageQueue.addAll(Splitter.fixedLength(Message.MAX_CONTENT_LENGTH - 2).splitToList(
                MessageUtil.formatMessage(jda.getSettings().getConsoleFormat(),
                        TimeFormat.TIME_LONG.format(Instant.now()),
                        event.getLevel().name(),
                        MessageUtil.sanitizeDiscordMarkdown(entry))));
    }

    public void remove() {
        ((Logger) LogManager.getRootLogger()).removeAppender(this);
        Bukkit.getScheduler().cancelTask(taskId);
        messageQueue.clear();
    }
}
