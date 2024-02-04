package com.earth2me.essentials.geoip;

import com.earth2me.essentials.EssentialsLogger;
import com.earth2me.essentials.metrics.MetricsWrapper;
import com.earth2me.essentials.utils.AdventureUtil;
import net.ess3.api.IEssentials;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tlLiteral;

public class EssentialsGeoIP extends JavaPlugin {

    private transient MetricsWrapper metrics = null;

    @Override
    public void onEnable() {
        EssentialsLogger.updatePluginLogger(this);
        final PluginManager pm = getServer().getPluginManager();
        final IEssentials ess = (IEssentials) pm.getPlugin("Essentials");
        if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, AdventureUtil.miniToLegacy(tlLiteral("versionMismatchAll")));
        }
        if (!ess.isEnabled()) {
            this.setEnabled(false);
            return;
        }

        Logger.getLogger(com.fasterxml.jackson.databind.ext.Java7Support.class.getName()).setLevel(Level.SEVERE);

        final EssentialsGeoIPPlayerListener playerListener = new EssentialsGeoIPPlayerListener(getDataFolder(), ess, this);
        pm.registerEvents(playerListener, this);

        getLogger().log(Level.INFO, "This product includes GeoLite2 data created by MaxMind, available from http://www.maxmind.com/.");

        if (metrics == null) {
            metrics = new MetricsWrapper(this, 3815, false);
        }
    }

}
