package com.earth2me.essentials.spawn;

import com.earth2me.essentials.EssentialsLogger;
import com.earth2me.essentials.metrics.MetricsWrapper;
import net.ess3.api.IEssentials;
import net.essentialsx.api.v2.events.AsyncUserDataLoadEvent;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tlLiteral;

public class EssentialsSpawn extends JavaPlugin implements IEssentialsSpawn {
    private transient IEssentials ess;
    private transient SpawnStorage spawns;
    private transient MetricsWrapper metrics = null;

    @Override
    public void onEnable() {
        EssentialsLogger.updatePluginLogger(this);
        final PluginManager pluginManager = getServer().getPluginManager();
        ess = (IEssentials) pluginManager.getPlugin("Essentials");
        if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, ess.getAdventureFacet().miniToLegacy(tlLiteral("versionMismatchAll")));
        }
        if (!ess.isEnabled()) {
            this.setEnabled(false);
            return;
        }

        spawns = new SpawnStorage(ess);
        ess.addReloadListener(spawns);

        final EssentialsSpawnPlayerListener playerListener = new EssentialsSpawnPlayerListener(ess, spawns);

        final EventPriority respawnPriority = ess.getSettings().getRespawnPriority();
        if (respawnPriority != null) {
            pluginManager.registerEvent(PlayerRespawnEvent.class, playerListener, respawnPriority, (ll, event) ->
                ((EssentialsSpawnPlayerListener) ll).onPlayerRespawn((PlayerRespawnEvent) event), this);
        }

        final EventPriority joinPriority = ess.getSettings().getSpawnJoinPriority();
        if (joinPriority != null) {
            // Listen on AsyncUserDataLoadEvent (fired after the core join flow) so we can reliably tell whether this is
            // a player's first join, instead of using the unreliable Player#hasPlayedBefore() (see GH-6466).
            pluginManager.registerEvent(AsyncUserDataLoadEvent.class, playerListener, joinPriority, (ll, event) ->
                ((EssentialsSpawnPlayerListener) ll).onUserDataLoad((AsyncUserDataLoadEvent) event), this);
        }

        if (metrics == null) {
            metrics = new MetricsWrapper(this, 3817, true);
        }
    }

    public static Logger getWrappedLogger() {
        try {
            return EssentialsLogger.getLoggerProvider("EssentialsSpawn");
        } catch (Throwable ignored) {
            // In case Essentials isn't installed/loaded
            return Logger.getLogger("EssentialsSpawn");
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        metrics.markCommand(command.getName(), true);
        return ess.onCommandEssentials(sender, command, commandLabel, args, EssentialsSpawn.class.getClassLoader(), "com.earth2me.essentials.spawn.Command", "essentials.", spawns);
    }

    @Override
    public void setSpawn(final Location loc, final String group) {
        if (group == null) {
            throw new IllegalArgumentException("Null group");
        }
        spawns.setSpawn(loc, group);
    }

    @Override
    public Location getSpawn(final String group) {
        if (group == null) {
            throw new IllegalArgumentException("Null group");
        }
        return spawns.getSpawn(group);
    }
}
