package net.essentialsx.discord;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.metrics.MetricsWrapper;
import net.essentialsx.discord.interactions.InteractionControllerImpl;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsDiscord extends JavaPlugin implements IEssentialsModule {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");
    private transient IEssentials ess;
    private transient MetricsWrapper metrics = null;

    private JDADiscordService jda;
    private DiscordSettings settings;
    private boolean isPAPI = false;

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

        isPAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        settings = new DiscordSettings(this);
        ess.addReloadListener(settings);

        if (jda == null) {
            jda = new JDADiscordService(this);
            try {
                jda.startup();
                ess.scheduleSyncDelayedTask(() -> ((InteractionControllerImpl) jda.getInteractionController()).processBatchRegistration());
            } catch (Exception e) {
                logger.log(Level.SEVERE, tl("discordErrorLogin", e.getMessage()));
                if (ess.getSettings().isDebug()) {
                    e.printStackTrace();
                }
                setEnabled(false);
                return;
            }
        }

        if (metrics == null) {
            metrics = new MetricsWrapper(this, 9824, false);
        }
    }

    public void onReload() {
        if (jda != null) {
            jda.updatePresence();
            jda.updatePrimaryChannel();
            jda.updateConsoleRelay();
            jda.updateTypesRelay();
        }
    }

    public IEssentials getEss() {
        return ess;
    }

    public DiscordSettings getSettings() {
        return settings;
    }

    public boolean isPAPI() {
        return isPAPI;
    }

    @Override
    public void onDisable() {
        if (jda != null) {
            jda.shutdown();
        }
    }
}
