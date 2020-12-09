package com.earth2me.essentials.tasks;

import com.earth2me.essentials.Essentials;
import me.rayzr522.jsonmessage.JSONMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class VanishTask extends BukkitRunnable {
    private final Essentials plugin;

    public VanishTask(Essentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> plugin.getVanishedPlayers().contains(player.getName()))
                .forEach(player -> JSONMessage.actionbar(ChatColor.translateAlternateColorCodes('&', "&a&lVANISHED"), player));
    }
}
