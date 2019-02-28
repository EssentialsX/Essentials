package com.earth2me.essentials.discord;

/**
 * Types of messages that can be sent to or received from Discord by this plugin.
 * Other plugins may also send messages, but these are not listed hre.
 */
public enum MessageType {

    GLOBAL_CHAT("chat", true, false),
    LOCAL_CHAT("local-chat", true, false),
    HELPOP("helpop", true, false),
    BROADCAST("broadcast"),
    PLAYER_JOIN("join"),
    PLAYER_LEAVE("leave"),
    PLAYER_DEATH("death"),
    PLAYER_ADVANCEMENT("advancement"),
    PLAYER_MUTE("mute"),
    PLAYER_KICK("kick"),
    PLAYER_BAN("ban"),
    CONSOLE_LOG("console", false, true),
    OTHER("other")
    ;

    private final String configName;
    private final boolean canWebhook;
    private final boolean shouldBatch;

    MessageType(final String configName) {
        this(configName, false, false);
    }

    MessageType(final String configName, final boolean canWebhook, final boolean shouldBatch) {
        this.configName = configName;
        this.canWebhook = canWebhook;
        this.shouldBatch = shouldBatch;
    }

    public String getConfigName() {
        return configName;
    }

    public boolean isCanWebhook() {
        return canWebhook;
    }

    public boolean isShouldBatch() {
        return shouldBatch;
    }
}
