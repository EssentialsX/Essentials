package com.earth2me.essentials.discord;

import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.metrics.Metrics;
import net.ess3.api.IEssentials;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsDiscord extends JavaPlugin implements IEssentialsModule {

    private static final Logger logger = Logger.getLogger("EssentialsDiscord");

    private transient Metrics metrics = null;
    private EssentialsDiscordListener discordListener;

    @Override
    public void onEnable() {
        final IEssentials ess = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
        if (!getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, tl("versionMismatchAll"));
        }

        if (!ess.isEnabled()) {
            setEnabled(false);
            return;
        }

        if (metrics == null) {
            metrics = new Metrics(this);
        }

        if (discordListener == null) {
            discordListener = new EssentialsDiscordListener(getDataFolder(), ess);
        }
        try {
            discordListener.startup();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
