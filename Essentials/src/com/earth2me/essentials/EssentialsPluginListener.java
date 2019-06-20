package com.earth2me.essentials;

import com.earth2me.essentials.register.payment.Methods;
import net.ess3.api.IEssentials;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.logging.Level;


/**
 * <p>EssentialsPluginListener class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class EssentialsPluginListener implements Listener, IConf {
    private final transient IEssentials ess;

    /**
     * <p>Constructor for EssentialsPluginListener.</p>
     *
     * @param ess a {@link net.ess3.api.IEssentials} object.
     */
    public EssentialsPluginListener(final IEssentials ess) {
        this.ess = ess;
    }

    /**
     * <p>onPluginEnable.</p>
     *
     * @param event a {@link org.bukkit.event.server.PluginEnableEvent} object.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(final PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("EssentialsChat")) {
            ess.getSettings().setEssentialsChatActive(true);
        }
        ess.getPermissionsHandler().setUseSuperperms(ess.getSettings().useBukkitPermissions());
        ess.getPermissionsHandler().checkPermissions();
        ess.getAlternativeCommandsHandler().addPlugin(event.getPlugin());
        if (!Methods.hasMethod() && Methods.setMethod(ess.getServer().getPluginManager())) {
            ess.getLogger().log(Level.INFO, "Payment method found (" + Methods.getMethod().getLongName() + " version: " + ess.getPaymentMethod().getMethod().getVersion() + ")");
        }
    }

    /**
     * <p>onPluginDisable.</p>
     *
     * @param event a {@link org.bukkit.event.server.PluginDisableEvent} object.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(final PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("EssentialsChat")) {
            ess.getSettings().setEssentialsChatActive(false);
        }
        ess.getAlternativeCommandsHandler().removePlugin(event.getPlugin());
        // Check to see if the plugin thats being disabled is the one we are using
        if (ess.getPaymentMethod() != null && Methods.hasMethod() && Methods.checkDisabled(event.getPlugin())) {
            Methods.reset();
            ess.getLogger().log(Level.INFO, "Payment method was disabled. No longer accepting payments.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void reloadConfig() {
    }
}
