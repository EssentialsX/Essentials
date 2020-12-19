package net.essentialsx.discord.listeners;

import net.essentialsx.discord.EssentialsJDA;
import org.bukkit.event.Listener;

public class BukkitListener implements Listener {
    private final EssentialsJDA jda;

    public BukkitListener(EssentialsJDA jda) {
        this.jda = jda;
    }
}
