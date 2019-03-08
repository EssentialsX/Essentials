package com.earth2me.essentials.discord;

import org.bukkit.Bukkit;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

/**
 * Created by GlareMasters
 * Date: 2/28/2019
 * Time: 10:35 PM
 */
public class DiscordListener implements MessageCreateListener {

    private final DiscordApi api;

    public DiscordListener(DiscordApi api) {
        this.api = api;
    }

    /**
     * Send a message to the Minecraft Server with the message from the Discord
     * @param event
     */
    // todo Make this more in-depth to allow which channels it can come from
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageAuthor().isWebhook() || !event.getMessageAuthor().isUser()) return;
        Bukkit.broadcastMessage(event.getMessageAuthor().getDiscriminatedName() + " : " + event.getMessageContent());
    }
}
