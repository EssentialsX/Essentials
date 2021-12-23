package net.ess3.provider.providers;

import io.papermc.paper.advancement.AdvancementDisplay;
import io.papermc.paper.text.PaperComponents;
import net.ess3.provider.AbstractAchievementEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class PaperAdvancementListenerProvider implements Listener {
    private final ComponentSerializer<Component, TextComponent, String> serializer;

    public PaperAdvancementListenerProvider() {
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
    public void onAdvancement(final PlayerAdvancementDoneEvent event) {
        final AdvancementDisplay display = event.getAdvancement().getDisplay();
        if (display != null && display.doesAnnounceToChat()) {
            Bukkit.getPluginManager().callEvent(new AbstractAchievementEvent(event.getPlayer(), serializer.serialize(display.title())));
        }
    }
}
