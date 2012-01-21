package com.nijikokun.bukkit.Permissions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.Listener;



public class OverrideListener implements Listener {
	
	Permissions permClass;
	
	OverrideListener(Permissions instance) {
		this.permClass = instance;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getDescription().getName().equals("GroupManager")) {
        	permClass.setGM(event.getPlugin());
        }
    }
	
}