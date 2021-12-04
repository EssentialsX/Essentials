package net.ess3.provider.providers;

import net.ess3.provider.CommandSendListenerProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.function.Predicate;

public class BukkitCommandSendListenerProvider extends CommandSendListenerProvider {
    public BukkitCommandSendListenerProvider(Filter commandFilter) {
        super(commandFilter);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommandSend(PlayerCommandSendEvent event) {
        final Predicate<String> filter = filter(event.getPlayer());
        event.getCommands().removeIf(filter);
    }

    @Override
    public String getDescription() {
        return "Bukkit synchronous command send listener";
    }
}
