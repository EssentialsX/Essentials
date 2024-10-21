package net.ess3.provider.providers;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.text.PaperComponents;
import net.ess3.provider.AbstractAsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

public class PaperAsyncChatListenerProvider implements Listener {
    private final ComponentSerializer<Component, TextComponent, String> serializer;
    private final JavaPlugin plugin;

    public PaperAsyncChatListenerProvider(JavaPlugin plugin) {
        this.plugin = plugin;
        ComponentSerializer<Component, TextComponent, String> yeOldSerializer;
        try {
            // This method is only available in Paper 1.18.1+ and replaces the old deprecated method below.
            yeOldSerializer = PaperComponents.plainTextSerializer();
        } catch (NoSuchMethodError e) {
            //noinspection deprecation
            yeOldSerializer = PaperComponents.plainSerializer();
        }
        this.serializer = yeOldSerializer;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncChatEvent(final AsyncChatEvent event) {
        Bukkit.getPluginManager().callEvent(
                new AbstractAsyncChatEvent(
                        event.isAsynchronous(),
                        event.getPlayer(),
                        serializer.serialize(event.message()),
                        event.viewers().stream()
                                .filter(v -> v instanceof Player)
                                .map(v -> (Player) v)
                                .collect(Collectors.toSet())
                )
        );
    }
}
