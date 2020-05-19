package com.earth2me.essentials.discord;

import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.metrics.Metrics;
import net.ess3.api.IEssentials;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsDiscord extends JavaPlugin implements IEssentialsModule {

    private static final Logger logger = Logger.getLogger("EssentialsDiscord");

    private transient Metrics metrics = null;
    private transient IEssentials ess;
    private EssentialsJDA essJDA;
    private DiscordSettings settings;

    @Override
    public void onEnable() {
        final IEssentials ess = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
        if (ess == null || !ess.isEnabled()) {
            setEnabled(false);
            return;
        }
        if (!getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, tl("versionMismatchAll"));
        }

        settings = new DiscordSettings(this);
        ess.addReloadListener(settings);

        if (essJDA == null) {
            essJDA = new EssentialsJDA(this);
        }
        try {
            essJDA.startup();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        if (metrics == null) {
            metrics = new Metrics(this);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public DiscordSettings getSettings() {
        return settings;
    }

    public IEssentials getParent() {
        return ess;
    }
}
