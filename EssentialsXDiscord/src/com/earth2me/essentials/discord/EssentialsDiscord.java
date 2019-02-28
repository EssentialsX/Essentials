package com.earth2me.essentials.discord;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.metrics.Metrics;
import net.ess3.api.IEssentials;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsDiscord extends JavaPlugin implements IEssentialsModule, IConf {

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

        final EssentialsDiscordListener discordListener = new EssentialsDiscordListener(getDataFolder(), ess);
        pm.registerEvents(discordListener, this);


        if (metrics == null) {
            metrics = new Metrics(this);
        }
    }
}
