package net.essentialsx.discordlink;

import com.earth2me.essentials.EssentialsLogger;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.metrics.MetricsWrapper;
import com.google.common.collect.ImmutableSet;
import net.essentialsx.api.v2.services.discord.DiscordService;
import net.essentialsx.api.v2.services.discord.InteractionException;
import net.essentialsx.api.v2.services.discordlink.DiscordLinkService;
import net.essentialsx.discord.EssentialsDiscord;
import net.essentialsx.discordlink.commands.discord.AccountInteractionCommand;
import net.essentialsx.discordlink.commands.discord.LinkInteractionCommand;
import net.essentialsx.discordlink.commands.discord.UnlinkInteractionCommand;
import net.essentialsx.discordlink.listeners.LinkBukkitListener;
import net.essentialsx.discordlink.rolesync.RoleSyncManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tlLiteral;

public class EssentialsDiscordLink extends JavaPlugin {
    private transient IEssentials ess;
    private transient MetricsWrapper metrics = null;

    private DiscordService api;
    private DiscordLinkSettings settings;
    private AccountStorage accounts;
    private AccountLinkManager linkManager;
    private RoleSyncManager roleSyncManager;

    @Override
    public void onEnable() {
        ess = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
        final EssentialsDiscord essDiscord = (EssentialsDiscord) getServer().getPluginManager().getPlugin("EssentialsDiscord");
        if (ess == null || !ess.isEnabled() || essDiscord == null || !essDiscord.isEnabled()) {
            setEnabled(false);
            return;
        }
        if (!getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, tlLiteral("versionMismatchAll"));
        }

        api = getServer().getServicesManager().load(DiscordService.class);

        settings = new DiscordLinkSettings(this);
        ess.addReloadListener(settings);
        try {
            accounts = new AccountStorage(this);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Unable to create link accounts file", e);
            setEnabled(false);
            return;
        }

        roleSyncManager = new RoleSyncManager(this);
        linkManager = new AccountLinkManager(this, accounts, roleSyncManager);

        getServer().getPluginManager().registerEvents(new LinkBukkitListener(this), this);
        getServer().getServicesManager().register(DiscordLinkService.class, linkManager, this, ServicePriority.Normal);

        if (!(api.getInteractionController().getCommand("link") instanceof LinkInteractionCommand)) {
            try {
                api.getInteractionController().registerCommand(new AccountInteractionCommand(linkManager));
                api.getInteractionController().registerCommand(new LinkInteractionCommand(linkManager));
                api.getInteractionController().registerCommand(new UnlinkInteractionCommand(linkManager));
            } catch (InteractionException e) {
                e.printStackTrace();
                setEnabled(false);
                return;
            }
        }

        ess.getPermissionsHandler().registerContext("essentials:linked", user ->
                Collections.singleton(String.valueOf(linkManager.isLinked(user.getUUID()))), () -> ImmutableSet.of("true", "false"));

        if (metrics == null) {
            metrics = new MetricsWrapper(this, 11462, false);
        }
    }

    @Override
    public void onDisable() {
        if (accounts != null) {
            accounts.shutdown();
        }
    }

    public void onReload() {
        if (roleSyncManager != null) {
            roleSyncManager.onReload();
        }
    }

    public IEssentials getEss() {
        return ess;
    }

    public DiscordService getApi() {
        return api;
    }

    public DiscordLinkSettings getSettings() {
        return settings;
    }

    public AccountStorage getAccountStorage() {
        return accounts;
    }

    public AccountLinkManager getLinkManager() {
        return linkManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return ess.onCommandEssentials(sender, command, label, args, EssentialsDiscordLink.class.getClassLoader(), "net.essentialsx.discordlink.commands.bukkit.Command", "essentials.", linkManager);
    }

    @Override
    public Logger getLogger() {
        try {
            return EssentialsLogger.getLoggerProvider(this);
        } catch (Throwable ignored) {
            // In case Essentials isn't installed/loaded
            return super.getLogger();
        }
    }
}
