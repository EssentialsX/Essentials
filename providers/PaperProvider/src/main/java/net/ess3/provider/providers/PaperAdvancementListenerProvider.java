package net.ess3.provider.providers;

import io.papermc.paper.advancement.AdvancementDisplay;
import net.ess3.provider.AbstractAchievementEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class PaperAdvancementListenerProvider implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAdvancement(final PlayerAdvancementDoneEvent event) {
        final AdvancementDisplay display = event.getAdvancement().getDisplay();
        if (display != null && display.doesAnnounceToChat()) {
            //noinspection deprecation
            Bukkit.getPluginManager().callEvent(new AbstractAchievementEvent(event.getPlayer(), Bukkit.getUnsafe().plainComponentSerializer().serialize(display.title())));
        }
    }
}
