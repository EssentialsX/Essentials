package net.essentialsx.discord;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.metrics.MetricsWrapper;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsDiscord extends JavaPlugin implements IEssentialsModule {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");
    private transient IEssentials ess;
    private transient MetricsWrapper metrics = null;

    private EssentialsJDA jda;
    private DiscordSettings settings;

    @Override
    public void onEnable() {
        ess = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
        if (ess == null || !ess.isEnabled()) {
            setEnabled(false);
            return;
        }
        if (!getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, tl("versionMismatchAll"));
        }

        settings = new DiscordSettings(this);
        ess.addReloadListener(settings);

        if (jda == null) {
            jda = new EssentialsJDA(this);
        }

        try {
            jda.startup();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while logging into discord: " + e.getMessage());
            if (ess.getSettings().isDebug()) {
                e.printStackTrace();
            }
            setEnabled(false);
            return;
        }

        //if (metrics == null) {
        //    metrics = new MetricsWrapper(this, 6969, false);
        //}
    }

    public void onReload() {
        if (jda != null) {
            jda.updatePresence();
            jda.updatePrimaryChannel();
        }
    }

    public IEssentials getEss() {
        return ess;
    }

    public DiscordSettings getSettings() {
        return settings;
    }

    @Override
    public void onDisable() {
        jda.shutdown();
    }
}
