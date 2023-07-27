package net.essentialsx.discord;

import com.earth2me.essentials.EssentialsLogger;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.metrics.MetricsWrapper;
import net.essentialsx.discord.interactions.InteractionControllerImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsDiscord extends JavaPlugin implements IEssentialsModule {
    private transient IEssentials ess;
    private transient MetricsWrapper metrics = null;

    private JDADiscordService jda;
    private DiscordSettings settings;
    private boolean isPAPI = false;
    private boolean isEssentialsChat = false;

    @Override
    public void onEnable() {
        EssentialsLogger.updatePluginLogger(this);
        ess = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
        if (ess == null || !ess.isEnabled()) {
            setEnabled(false);
            return;
        }
        if (!getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, tl("versionMismatchAll"));
        }

        // JDK-8274349 - Mitigation for a regression in Java 17 on 1 core systems which was fixed in 17.0.2
        final String[] javaVersion = System.getProperty("java.version").split("\\.");
        if (Runtime.getRuntime().availableProcessors() <= 1 && javaVersion[0].startsWith("17") && (javaVersion.length < 2 || (javaVersion[1].equals("0") && javaVersion[2].startsWith("1")))) {
            getLogger().log(Level.INFO, "Essentials is mitigating JDK-8274349");
            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
        }

        isPAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        isEssentialsChat = getServer().getPluginManager().getPlugin("EssentialsChat") != null;

        settings = new DiscordSettings(this);
        ess.addReloadListener(settings);

        if (metrics == null) {
            metrics = new MetricsWrapper(this, 9824, false);
        }

        if (jda == null) {
            jda = new JDADiscordService(this);
            try {
                jda.startup();
                ess.scheduleInitTask(() -> ((InteractionControllerImpl) jda.getInteractionController()).processBatchRegistration());
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, tl("discordErrorLogin", e.getMessage()));
                if (ess.getSettings().isDebug()) {
                    e.printStackTrace();
                }
                jda.shutdown();
            }
        }
    }

    public static Logger getWrappedLogger() {
        try {
            return EssentialsLogger.getLoggerProvider("EssentialsDiscord");
        } catch (Throwable ignored) {
            // In case Essentials isn't installed/loaded
            return Logger.getLogger("EssentialsDiscord");
        }
    }

    public void onReload() {
        if (jda != null && !jda.isInvalidStartup()) {
            jda.updateListener();
            jda.updatePresence();
            jda.updatePrimaryChannel();
            jda.updateConsoleRelay();
            jda.updateTypesRelay();
        }
    }

    public boolean isInvalidStartup() {
        return jda != null && jda.isInvalidStartup();
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

    public boolean isEssentialsChat() {
        return isEssentialsChat;
    }

    @Override
    public void onDisable() {
        if (jda != null && !jda.isInvalidStartup()) {
            jda.shutdown();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return ess.onCommandEssentials(sender, command, label, args, EssentialsDiscord.class.getClassLoader(), "net.essentialsx.discord.commands.Command",
                "essentials.", jda);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return ess.onTabCompleteEssentials(sender, command, alias, args, EssentialsDiscord.class.getClassLoader(),
                "net.essentialsx.discord.commands.Command", "essentials.", jda);
    }
}
