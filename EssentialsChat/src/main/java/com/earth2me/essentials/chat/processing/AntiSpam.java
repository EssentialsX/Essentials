package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.Essentials;
import net.ess3.provider.AbstractChatEvent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AntiSpam {
    private static AntiSpam antiSpam;
    private final Essentials ess = Essentials.getPlugin(Essentials.class);

    private final FileConfiguration config;
    private final Map<UUID, String> lastMessage;
    private final Map<UUID, Long> cooldowns;

    public AntiSpam() {
        antiSpam = this;
        config = ess.getConfig();
        lastMessage = new ConcurrentHashMap<>();
        lastMessageTimer = new ConcurrentHashMap<>();
    }


    public void newMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        event.setMessage(capsFilter(message, player));
        event.setCancelled(duplicationFilter(message, player));
        event.setCancelled(chatCooldown(player));;
    }
    public void newMessagePaper(AbstractChatEvent event){
        Player player = event.getPlayer();
        String message = event.getMessage();

        event.setMessage(capsFilter(message, player));
        event.setCancelled(duplicationFilter(message, player));
        event.setCancelled(chatCooldown(player));;
    }
    private String capsFilter(String message, Player player) {
        if(ess.getConfig().getBoolean("chat.chat-caps-filter")) {

            int count = 0;

            for (int i = 0; i < message.length(); i++) {
                if (Character.isUpperCase(message.charAt(i))) {
                    count++;
                }
            }
            if (message.length() * 0.70 < count) {
                //return the message to chat with lowercase replaced
                player.sendMessage(Color.RED + "Please do not spam capital letters in chat");
                if(!Objects.requireNonNull(config.getString("caps-punish")).isEmpty()){
                    ess.getServer().dispatchCommand(ess.getServer().getConsoleSender(), Objects.requireNonNull(config.getString("caps-punish")));
                }
                return message.toLowerCase(Locale.ROOT);
            }
        }
        return message;
    }
    private boolean duplicationFilter(String message, Player player) {
        lastMessage.put(player.getUniqueId(), message.toLowerCase(Locale.ROOT));
        if(ess.getConfig().getBoolean("chat.duplicate-prevention")) {
            UUID uuid = player.getUniqueId();
            if (lastMessage.get(uuid).equalsIgnoreCase(message) ||
                    lastMessage.get(uuid).startsWith(message.toLowerCase(Locale.ROOT))||
                    lastMessage.get(uuid).endsWith(message.toLowerCase(Locale.ROOT))) {
                player.sendMessage(Color.RED + "Please do not send the same message twice.");
                if (!Objects.requireNonNull(config.getString("duplicate-punish")).isEmpty()) {
                    ess.getServer().dispatchCommand(ess.getServer().getConsoleSender(), Objects.requireNonNull(config.getString("duplicate-punish")));
                }
                lastMessage.put(player.getUniqueId(), message.toLowerCase());
                return true;
            }
        }
        lastMessage.put(player.getUniqueId(), message.toLowerCase());
        return false;
    }
    private boolean chatCooldown(Player player) {
        if(player.hasPermission("essentials.chat.bypass.cooldown")) return false;
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
            player.sendMessage(Color.RED + "There is currently a chat cooldown. Please wait " + lastMessage.get(player.getUniqueId()) + " seconds before sending another message.");
            return true;
        }
        return false;
    }
    public static AntiSpam getAntiSpam(){
        return antiSpam;
    }
}
