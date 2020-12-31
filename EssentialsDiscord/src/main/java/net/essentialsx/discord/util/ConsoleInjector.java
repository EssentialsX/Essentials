package net.essentialsx.discord.util;

import com.earth2me.essentials.utils.FormatUtil;
import com.google.common.base.Splitter;
import net.dv8tion.jda.api.entities.Message;
import net.essentialsx.discord.EssentialsJDA;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Plugin(name = "EssentialsX-ConsoleInjector", category = "Core", elementType = "appender", printObject = true)
public class ConsoleInjector extends AbstractAppender {
    private final EssentialsJDA jda;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss");

    public ConsoleInjector(EssentialsJDA jda) {
        super("EssentialsX-ConsoleInjector", null, null, false);
        this.jda = jda;
        ((Logger) LogManager.getRootLogger()).addAppender(this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(jda.getPlugin(), () -> {
            StringBuilder buffer = new StringBuilder();
            String curLine;
            while ((curLine = messageQueue.peek()) != null) {
                if (buffer.length() + curLine.length() > Message.MAX_CONTENT_LENGTH - 2) {
                    jda.getConsoleWebhook().send(jda.getWebhookMessage(buffer.toString()));
                    buffer = new StringBuilder();
                    continue;
                }
                buffer.append("\n").append(messageQueue.poll());
            }
            if (buffer.length() != 0) {
                jda.getConsoleWebhook().send(jda.getWebhookMessage(buffer.toString()));
            }
        }, 20, 40);
    }

    @Override
    public void append(LogEvent event) {
        if (event.getLevel().intLevel() > Level.INFO.intLevel()) {
            return;
        }

        // Ansi strip is for normal colors, normal strip is for 1.16 hex color codes as they are not formatted correctly
        final String entry = FormatUtil.stripFormat(FormatUtil.stripAnsi(event.getMessage().getFormattedMessage())).trim();
        if (entry.isEmpty()) {
            return;
        }

        //noinspection UnstableApiUsage
        messageQueue.addAll(Splitter.fixedLength(Message.MAX_CONTENT_LENGTH).splitToList(
                MessageUtil.formatMessage(jda.getSettings().getConsoleFormat(),
                        timestampFormat.format(new Date()),
                        event.getLevel().name(),
                        MessageUtil.sanitizeDiscordMarkdown(entry))));
    }

    public void remove() {
        ((Logger) LogManager.getRootLogger()).removeAppender(this);
    }
}
