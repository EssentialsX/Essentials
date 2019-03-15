package com.earth2me.essentials.spawn;

import com.earth2me.essentials.metrics.Metrics;
import net.ess3.api.IEssentials;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;


public class EssentialsSpawn extends JavaPlugin implements IEssentialsSpawn {
    private transient IEssentials ess;
    private transient SpawnStorage spawns;
    private transient Metrics metrics = null;

    @Override
    public void onEnable() {
        final PluginManager pluginManager = getServer().getPluginManager();
        ess = (IEssentials) pluginManager.getPlugin("Essentials");
        if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, tl("versionMismatchAll"));
        }
        if (!ess.isEnabled()) {
            this.setEnabled(false);
            return;
        }

        spawns = new SpawnStorage(ess);
        ess.addReloadListener(spawns);

        final EssentialsSpawnPlayerListener playerListener = new EssentialsSpawnPlayerListener(ess, spawns);

        EventPriority respawnPriority = ess.getSettings().getRespawnPriority();
        if (respawnPriority != null) {
            pluginManager.registerEvent(PlayerRespawnEvent.class, playerListener, respawnPriority, (ll, event) ->
                    ((EssentialsSpawnPlayerListener) ll).onPlayerRespawn((PlayerRespawnEvent) event), this);
        }

        EventPriority joinPriority = ess.getSettings().getSpawnJoinPriority();
        if (joinPriority != null) {
            pluginManager.registerEvent(PlayerJoinEvent.class, playerListener, joinPriority, (ll, event) ->
                    ((EssentialsSpawnPlayerListener) ll).onPlayerJoin((PlayerJoinEvent) event), this);
        }

        if (metrics == null) {
            metrics = new Metrics(this);
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        return ess.onCommandEssentials(sender, command, commandLabel, args, EssentialsSpawn.class.getClassLoader(), "com.earth2me.essentials.spawn.Command", "essentials.", spawns);
    }

    @Override
    public void setSpawn(Location loc, String group) {
        if (group == null) {
            throw new IllegalArgumentException("Null group");
        }
        spawns.setSpawn(loc, group);
    }

    @Override
    public Location getSpawn(String group) {
        if (group == null) {
            throw new IllegalArgumentException("Null group");
        }
        return spawns.getSpawn(group);
    }
}
