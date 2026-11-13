package com.earth2me.essentials.chat.processing;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectListner implements Listener {
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event){
        AntiSpam.getAntiSpam().unloadPlayer(event.getPlayer());
    }
}
