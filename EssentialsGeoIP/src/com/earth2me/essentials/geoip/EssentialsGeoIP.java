package com.earth2me.essentials.geoip;

import static com.earth2me.essentials.I18n.tl;

import com.earth2me.essentials.metrics.Metrics;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ess3.api.IEssentials;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsGeoIP extends JavaPlugin {
  
    private transient Metrics metrics = null;

    @Override
    public void onEnable() {
        final PluginManager pm = getServer().getPluginManager();
        final IEssentials ess = (IEssentials) pm.getPlugin("Essentials");
        if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, tl("versionMismatchAll"));
        }
        if (!ess.isEnabled()) {
            this.setEnabled(false);
            return;
        }

        Logger.getLogger(com.fasterxml.jackson.databind.ext.Java7Support.class.getName()).setLevel(Level.SEVERE);

        final EssentialsGeoIPPlayerListener playerListener = new EssentialsGeoIPPlayerListener(getDataFolder(), ess);
        pm.registerEvents(playerListener, this);


        getLogger().log(Level.INFO, "This product includes GeoLite2 data created by MaxMind, available from http://www.maxmind.com/.");

        if (metrics == null) {
            metrics = new Metrics(this);
        }
    }

}
