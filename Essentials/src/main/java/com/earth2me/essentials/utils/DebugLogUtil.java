package com.earth2me.essentials.utils;

import com.earth2me.essentials.Essentials;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;

public final class DebugLogUtil {
    private static Essentials essentials;

    private static final Appender APPENDER;

    static {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration cfg = ctx.getConfiguration();

        // if this ever breaks, we can do cfg.getAppenders() and look for instanceof RollingRandomAccessFileAppender
        APPENDER = cfg.getAppender("File");

        if (APPENDER == null) {
            System.out.println("Unable to find latest.log appender. Please report this to the EssX team!");
        }
    }

    private DebugLogUtil() {
    }

    public static void setEssentials(Essentials essentials) {
        DebugLogUtil.essentials = essentials;
    }

    public static void debugLog(final String message) {
        debugLog(message, null);
    }

    public static void debugLog(final String message, final Throwable throwable) {
        if (APPENDER == null || essentials != null && essentials.getSettings().isDebug()) {
            essentials.getLogger().log(java.util.logging.Level.INFO, message);
            return;
        }

        final Log4jLogEvent evt = Log4jLogEvent.newBuilder()
                .setLoggerName("EssentialsDebug")
                .setLevel(Level.INFO)
                .setMessage(new SimpleMessage(message))
                .setThrown(throwable)
                .build();
        APPENDER.append(evt);
    }

}
