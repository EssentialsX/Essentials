package com.earth2me.essentials;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import net.ess3.api.IEssentials;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EssentialsPaperListener implements Listener {
    private final net.ess3.api.IEssentials ess;

    public EssentialsPaperListener(IEssentials ess) {
        this.ess = ess;
    }

    @EventHandler
    public void onPlayerRecipeBookClick(PlayerRecipeBookClickEvent event) {
        if (ess.getUser(event.getPlayer()).isRecipeSee()) {
            event.setCancelled(true);
        }
    }
}
