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
    private final Essentials ess;

    private final FileConfiguration config;
    private final Map<UUID, String> lastMessage;
    private final Map<UUID, Long> cooldowns;

    public AntiSpam() {
        this(Essentials.getPlugin(Essentials.class));
    }

    public AntiSpam(Essentials ess) {
        this.ess = ess;
        antiSpam = this;
        config = ess.getConfig();
        lastMessage = new ConcurrentHashMap<>();
        cooldowns = new ConcurrentHashMap<>();
        ess.getServer().getPluginManager().registerEvents(new PlayerDisconnectListner(), ess);
    }

    public void newMessage(AbstractChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final String message = event.getMessage();

        event.setMessage(capsFilter(message, player));

        if (duplicationFilter(message, player)) {
            event.setCancelled(true);
            return;
        }

        if (chatCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        lastMessage.put(player.getUniqueId(), message.toLowerCase(Locale.ROOT));
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
                player.sendMessage(ChatColor.RED + "Please do not spam capital letters in chat");
                final String command = config.getString("caps-punish");

                if (command != null && !command.trim().isEmpty()) {
                    ess.getServer().dispatchCommand(
                            ess.getServer().getConsoleSender(),
                            command.replace("{player}", player.getName())
                    );
                }
                return message.toLowerCase(Locale.ROOT);
            }
        }
        return message;

    }

    private boolean duplicationFilter(String message, Player player) {
        if (!config.getBoolean("chat.duplicate-prevention")) {
            return false;
        }

        final UUID uuid = player.getUniqueId();
        final String normalizedMessage = message.toLowerCase(Locale.ROOT);
        final String previousMessage = lastMessage.get(uuid);

        if (previousMessage != null
                && (previousMessage.equals(normalizedMessage)
                || previousMessage.startsWith(normalizedMessage)
                || previousMessage.endsWith(normalizedMessage))) {
            final String command = config.getString("duplication-punish");
            if (command != null && !command.trim().isEmpty()) {
                ess.getServer().dispatchCommand(
                        ess.getServer().getConsoleSender(),
                        command.replace("{player}", player.getName())
                );
            }
            player.sendMessage(ChatColor.RED + "Please do not send the same message twice.");
            return true;
        }
        lastMessage.put(uuid, normalizedMessage);
        return false;
    }

    private boolean chatCooldown(Player player) {
        if (player.hasPermission("essentials.chat.bypass.cooldown")
                || !config.getBoolean("chat.chat-cooldown-enable")) {
            return false;
        }
        final long cooldownTime = config.getInt("chat.chat-cooldown-time") * 1000L;
        final UUID uuid = player.getUniqueId();
        final long currentTime = System.currentTimeMillis();
        final long lastMessageTime = cooldowns.getOrDefault(uuid, 0L);
        final long remaining = cooldownTime - (currentTime - lastMessageTime);

        if (remaining > 0) {
            player.sendMessage(ChatColor.RED + "Please wait "
                    + (remaining / 1000 + 1)
                    + " seconds before sending another message.");
            return true;
        }

        cooldowns.put(uuid, currentTime);
        return false;
    }

    public static AntiSpam getAntiSpam(){
        return antiSpam;
    }

    public void unloadPlayer(Player player){
        lastMessage.remove(player.getUniqueId());
        cooldowns.remove(player.getUniqueId());
    }
}
