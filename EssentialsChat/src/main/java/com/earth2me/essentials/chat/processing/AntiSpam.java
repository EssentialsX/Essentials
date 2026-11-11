package com.earth2me.essentials.chat.processing;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AntiSpam {
    static AntiSpam antiSpam;
    public AntiSpam() {
        antiSpam = this;
    }
    //Needs a listener, last message cache, compare to last message,
    Map<UUID, String> lastMessage = new HashMap<>();
    public void newMessage(Player player, String message, AsyncPlayerChatEvent event) {
        UUID uuid = player.getUniqueId();
        String storedMessage = lastMessage.get(uuid);
        if(storedMessage.equalsIgnoreCase(message) || storedMessage.startsWith(message)) {
            player.sendMessage("Please do not send the same message twice.");
            event.setCancelled(true);
            return;
        }
        if(storedMessage.endsWith(message)) {
            player.sendMessage("Please do not send the same message twice.");
            event.setCancelled(true);
            return;}
        int count = 0;

        for (int i = 0; i < storedMessage.length(); i++) {
            if (Character.isUpperCase(storedMessage.charAt(i))) {
                count++;
            }
        }
        if(storedMessage.length() * 0.70 > count) {
            //return the message to chat with lowercase replaced
            event.setMessage(storedMessage.toLowerCase());
            player.sendMessage("Please do not spam capital letters in chat");
            event.setMessage(storedMessage.toLowerCase());

            return;}
        lastMessage.put(uuid, message);
    }
    public static AntiSpam getAntiSpam(){
        return antiSpam;
    }
}
