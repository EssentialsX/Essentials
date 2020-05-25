package com.earth2me.essentials;

import com.earth2me.essentials.register.payment.Methods;
import com.google.common.collect.ImmutableSet;
import net.ess3.api.IEssentials;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.Collections;
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
        ess.getPermissionsHandler().registerContext("ess-afk", player -> Collections.singleton(String.valueOf(ess.getUser(player).isAfk())), () -> ImmutableSet.of("true", "false"));
        ess.getPermissionsHandler().registerContext("ess-muted", player -> Collections.singleton(String.valueOf(ess.getUser(player).isMuted())), () -> ImmutableSet.of("true", "false"));
        ess.getPermissionsHandler().registerContext("ess-vanished", player -> Collections.singleton(String.valueOf(ess.getUser(player).isHidden())), () -> ImmutableSet.of("true", "false"));
        ess.getAlternativeCommandsHandler().addPlugin(event.getPlugin());
        if (!Methods.hasMethod() && Methods.setMethod(ess.getServer().getPluginManager())) {
            ess.getLogger().log(Level.INFO, "Payment method found (" + Methods.getMethod().getLongName() + " version: " + ess.getPaymentMethod().getMethod().getVersion() + ")");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(final PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("EssentialsChat")) {
            ess.getSettings().setEssentialsChatActive(false);
        }
        ess.getPermissionsHandler().unregisterContexts();
        ess.getAlternativeCommandsHandler().removePlugin(event.getPlugin());
        // Check to see if the plugin thats being disabled is the one we are using
        if (ess.getPaymentMethod() != null && Methods.hasMethod() && Methods.checkDisabled(event.getPlugin())) {
            Methods.reset();
            ess.getLogger().log(Level.INFO, "Payment method was disabled. No longer accepting payments.");
        }
    }

    @Override
    public void reloadConfig() {
    }
}
