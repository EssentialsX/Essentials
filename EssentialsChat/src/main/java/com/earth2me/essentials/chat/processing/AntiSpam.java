package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.Essentials;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AntiSpam {
    static AntiSpam antiSpam;

    Essentials ess =  Essentials.getPlugin(Essentials.class);
    private final FileConfiguration config;

    private final Map<UUID, String> lastMessage;
    private final Map<UUID, Integer> lastMessageTimer;
    private BukkitTask runable;

    public AntiSpam() {
        antiSpam = this;
        config = ess.getConfig();
        lastMessage = new ConcurrentHashMap<>();
        lastMessageTimer = new ConcurrentHashMap<>();
    }


    public void newMessage(Player player, String message, AsyncPlayerChatEvent event) {
        capsFilter(message, player, event);
        duplicationFilter(message, player, event);
        chatCooldown(player, event);

    }
    private void capsFilter(String message, Player player, AsyncPlayerChatEvent event) {
        if(ess.getConfig().getBoolean("chat.chat-caps-filter")) {

            int count = 0;

            for (int i = 0; i < message.length(); i++) {
                if (Character.isUpperCase(message.charAt(i))) {
                    count++;
                }
            }
            if (message.length() * 0.70 < count) {
                //return the message to chat with lowercase replaced
                event.setMessage(message.toLowerCase());
                player.sendMessage(Color.RED + "Please do not spam capital letters in chat");
                if(!Objects.requireNonNull(config.getString("caps-punish")).isEmpty()){
                    ess.getServer().dispatchCommand(ess.getServer().getConsoleSender(), Objects.requireNonNull(config.getString("caps-punish")));
                }

                return;
            }
            lastMessage.put(player.getUniqueId(), message.toLowerCase());
        }
    }
    private void duplicationFilter(String message, Player player, AsyncPlayerChatEvent event) {
        if(ess.getConfig().getBoolean("chat.duplicate-prevention")) {
            UUID uuid = player.getUniqueId();
            if (lastMessage.get(uuid).equalsIgnoreCase(message) || lastMessage.get(uuid).startsWith(message)|| lastMessage.get(uuid).endsWith(message)) {
                player.sendMessage(Color.RED + "Please do not send the same message twice.");
                event.setCancelled(true);
                if (!Objects.requireNonNull(config.getString("duplicate-punish")).isEmpty()) {
                    ess.getServer().dispatchCommand(ess.getServer().getConsoleSender(), Objects.requireNonNull(config.getString("duplicate-punish")));
                }
            }
        }
    }
    private void chatCooldown(Player player, AsyncPlayerChatEvent event) {
        if(player.hasPermission("essentials.chat.bypass.cooldown")) return;
        if(ess.getConfig().getBoolean("chat.chat-cooldown-enable")){
            //if task is shutdown for some reason, recreate it
            if(runable == null){
                runable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for(Map.Entry<UUID, Integer> entry : lastMessageTimer.entrySet()){
                            if(lastMessageTimer.get(entry.getKey()) == null){
                                lastMessageTimer.putIfAbsent(entry.getKey(), 0);
                            }
                            lastMessageTimer.put(entry.getKey(), entry.getValue() +1);
                            if(entry.getValue() >= ess.getConfig().getInt("chat.chat-cooldown-time")){
                                lastMessageTimer.remove(entry.getKey());

                            }
                        }
                    }
                }.runTaskTimer(ess, 20L, 20L);
            }
        }
        if (lastMessageTimer.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(Color.RED + "There is currently a chat cooldown. Please wait " + lastMessage.get(player.getUniqueId()) + " seconds before sending another message.");
        }
    }
    public static AntiSpam getAntiSpam(){
        return antiSpam;
    }
}
