package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.IEssentials;
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
    private final IEssentials ess;

    private final FileConfiguration config;
    private final Map<UUID, String> lastMessage;
    private final Map<UUID, Long> cooldowns;

    public AntiSpam(IEssentials ess) {
        this.ess = ess;
        antiSpam = this;
        config = ess.getConfig();
        lastMessage = new ConcurrentHashMap<>();
        cooldowns = new ConcurrentHashMap<>();
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

        if (previousMessage != null && isSimilar(previousMessage, normalizedMessage)) {
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

    private int levenshteinDistance(String a, String b) {
        final int[] costs = new int[b.length() + 1];

        for (int j = 0; j <= b.length(); j++) {
            costs[j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int previousCost = i - 1;

            for (int j = 1; j <= b.length(); j++) {
                final int currentCost = costs[j];

                costs[j] = Math.min(
                        Math.min(costs[j] + 1, costs[j - 1] + 1),
                        previousCost + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1)
                );

                previousCost = currentCost;
            }
        }

        return costs[b.length()];
    }

    private boolean isSimilar(String a, String b) {
        if (a.equals(b)) {
            return true;
        }

        final int distance = levenshteinDistance(a, b);
        final int maxLength = Math.max(a.length(), b.length());

        return distance <= 1
                || 1.0 - (double) distance / maxLength >= 0.85;
    }
}
