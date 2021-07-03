package com.earth2me.essentials;

import com.earth2me.essentials.economy.EconomyLayer;
import com.earth2me.essentials.economy.EconomyLayers;
import net.ess3.api.IEssentials;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.logging.Level;

public class EssentialsPluginListener implements Listener, IConf {
    private final transient IEssentials ess;

    public EssentialsPluginListener(final IEssentials ess) {
        this.ess = ess;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(final PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("EssentialsChat")) {
            ess.getSettings().setEssentialsChatActive(true);
        }
        ess.getPermissionsHandler().setUseSuperperms(ess.getSettings().useBukkitPermissions());
        ess.getPermissionsHandler().checkPermissions();
        ess.getAlternativeCommandsHandler().addPlugin(event.getPlugin());
        if (EconomyLayers.isServerStarted()) {
            final EconomyLayer layer = EconomyLayers.onPluginEnable(event.getPlugin());
            if (layer != null) {
                ess.getLogger().log(Level.INFO, "Essentials found a compatible payment resolution method: " + layer.getName() + " (v" + layer.getPluginVersion() + ")!");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(final PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("EssentialsChat")) {
            ess.getSettings().setEssentialsChatActive(false);
        }
        ess.getPermissionsHandler().checkPermissions();
        ess.getAlternativeCommandsHandler().removePlugin(event.getPlugin());
        if (EconomyLayers.onPluginDisable(event.getPlugin())) {
            final EconomyLayer layer = EconomyLayers.getSelectedLayer();
            if (layer != null) {
                ess.getLogger().log(Level.INFO, "Essentials found a new compatible payment resolution method: " + layer.getName() + " (v" + layer.getPluginVersion() + ")!");
            } else {
                ess.getLogger().log(Level.INFO, "Active payment resolution method has been disabled! Falling back to Essentials' default payment resolution system!");
            }
        }
    }

    @Override
    public void reloadConfig() {
    }
}
