package com.earth2me.essentials;

import com.earth2me.essentials.api.IItemDb;
import com.earth2me.essentials.api.IJails;
import com.earth2me.essentials.api.IWarps;
import com.earth2me.essentials.metrics.Metrics;
import com.earth2me.essentials.perm.PermissionsHandler;
import com.earth2me.essentials.register.payment.Methods;
import net.ess3.nms.SpawnerProvider;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * <p>IEssentials interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IEssentials extends Plugin {
    /**
     * <p>addReloadListener.</p>
     *
     * @param listener a {@link com.earth2me.essentials.IConf} object.
     */
    void addReloadListener(IConf listener);

    /**
     * <p>reload.</p>
     */
    void reload();

    /**
     * <p>onTabCompleteEssentials.</p>
     *
     * @param sender a {@link org.bukkit.command.CommandSender} object.
     * @param command a {@link org.bukkit.command.Command} object.
     * @param commandLabel a {@link java.lang.String} object.
     * @param args an array of {@link java.lang.String} objects.
     * @param classLoader a {@link java.lang.ClassLoader} object.
     * @param commandPath a {@link java.lang.String} object.
     * @param permissionPrefix a {@link java.lang.String} object.
     * @param module a {@link com.earth2me.essentials.IEssentialsModule} object.
     * @return a {@link java.util.List} object.
     */
    List<String> onTabCompleteEssentials(CommandSender sender, Command command, String commandLabel, String[] args, ClassLoader classLoader, String commandPath, String permissionPrefix, IEssentialsModule module);

    /**
     * <p>onCommandEssentials.</p>
     *
     * @param sender a {@link org.bukkit.command.CommandSender} object.
     * @param command a {@link org.bukkit.command.Command} object.
     * @param commandLabel a {@link java.lang.String} object.
     * @param args an array of {@link java.lang.String} objects.
     * @param classLoader a {@link java.lang.ClassLoader} object.
     * @param commandPath a {@link java.lang.String} object.
     * @param permissionPrefix a {@link java.lang.String} object.
     * @param module a {@link com.earth2me.essentials.IEssentialsModule} object.
     * @return a boolean.
     */
    boolean onCommandEssentials(CommandSender sender, Command command, String commandLabel, String[] args, ClassLoader classLoader, String commandPath, String permissionPrefix, IEssentialsModule module);

    /**
     * <p>getUser.</p>
     *
     * @param base a {@link java.lang.Object} object.
     * @return a {@link com.earth2me.essentials.User} object.
     */
    @Deprecated
    User getUser(Object base);

    /**
     * <p>getUser.</p>
     *
     * @param base a {@link java.util.UUID} object.
     * @return a {@link com.earth2me.essentials.User} object.
     */
    User getUser(UUID base);

    /**
     * <p>getUser.</p>
     *
     * @param base a {@link java.lang.String} object.
     * @return a {@link com.earth2me.essentials.User} object.
     */
    User getUser(String base);

    /**
     * <p>getUser.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     * @return a {@link com.earth2me.essentials.User} object.
     */
    User getUser(Player base);

    /**
     * <p>getI18n.</p>
     *
     * @return a {@link com.earth2me.essentials.I18n} object.
     */
    I18n getI18n();

    /**
     * <p>getOfflineUser.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link com.earth2me.essentials.User} object.
     */
    User getOfflineUser(String name);

    /**
     * <p>getWorld.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.bukkit.World} object.
     */
    World getWorld(String name);

    /**
     * <p>broadcastMessage.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @return a int.
     */
    int broadcastMessage(String message);

    /**
     * <p>broadcastMessage.</p>
     *
     * @param sender a {@link com.earth2me.essentials.IUser} object.
     * @param message a {@link java.lang.String} object.
     * @return a int.
     */
    int broadcastMessage(IUser sender, String message);

    /**
     * <p>broadcastMessage.</p>
     *
     * @param permission a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     * @return a int.
     */
    int broadcastMessage(String permission, String message);

    /**
     * <p>getSettings.</p>
     *
     * @return a {@link com.earth2me.essentials.ISettings} object.
     */
    ISettings getSettings();

    /**
     * <p>getScheduler.</p>
     *
     * @return a {@link org.bukkit.scheduler.BukkitScheduler} object.
     */
    BukkitScheduler getScheduler();

    /**
     * <p>getJails.</p>
     *
     * @return a {@link com.earth2me.essentials.api.IJails} object.
     */
    IJails getJails();

    /**
     * <p>getWarps.</p>
     *
     * @return a {@link com.earth2me.essentials.api.IWarps} object.
     */
    IWarps getWarps();

    /**
     * <p>getWorth.</p>
     *
     * @return a {@link com.earth2me.essentials.Worth} object.
     */
    Worth getWorth();

    /**
     * <p>getBackup.</p>
     *
     * @return a {@link com.earth2me.essentials.Backup} object.
     */
    Backup getBackup();

    /**
     * <p>getKits.</p>
     *
     * @return a {@link com.earth2me.essentials.Kits} object.
     */
    Kits getKits();

    /**
     * <p>getPaymentMethod.</p>
     *
     * @return a {@link com.earth2me.essentials.register.payment.Methods} object.
     */
    Methods getPaymentMethod();

    /**
     * <p>runTaskAsynchronously.</p>
     *
     * @param run a {@link java.lang.Runnable} object.
     * @return a {@link org.bukkit.scheduler.BukkitTask} object.
     */
    BukkitTask runTaskAsynchronously(Runnable run);

    /**
     * <p>runTaskLaterAsynchronously.</p>
     *
     * @param run a {@link java.lang.Runnable} object.
     * @param delay a long.
     * @return a {@link org.bukkit.scheduler.BukkitTask} object.
     */
    BukkitTask runTaskLaterAsynchronously(Runnable run, long delay);

    /**
     * <p>runTaskTimerAsynchronously.</p>
     *
     * @param run a {@link java.lang.Runnable} object.
     * @param delay a long.
     * @param period a long.
     * @return a {@link org.bukkit.scheduler.BukkitTask} object.
     */
    BukkitTask runTaskTimerAsynchronously(Runnable run, long delay, long period);

    /**
     * <p>scheduleSyncDelayedTask.</p>
     *
     * @param run a {@link java.lang.Runnable} object.
     * @return a int.
     */
    int scheduleSyncDelayedTask(Runnable run);

    /**
     * <p>scheduleSyncDelayedTask.</p>
     *
     * @param run a {@link java.lang.Runnable} object.
     * @param delay a long.
     * @return a int.
     */
    int scheduleSyncDelayedTask(Runnable run, long delay);

    /**
     * <p>scheduleSyncRepeatingTask.</p>
     *
     * @param run a {@link java.lang.Runnable} object.
     * @param delay a long.
     * @param period a long.
     * @return a int.
     */
    int scheduleSyncRepeatingTask(Runnable run, long delay, long period);

    /**
     * <p>getTNTListener.</p>
     *
     * @return a {@link com.earth2me.essentials.TNTExplodeListener} object.
     */
    TNTExplodeListener getTNTListener();

    /**
     * <p>getPermissionsHandler.</p>
     *
     * @return a {@link com.earth2me.essentials.perm.PermissionsHandler} object.
     */
    PermissionsHandler getPermissionsHandler();

    /**
     * <p>getAlternativeCommandsHandler.</p>
     *
     * @return a {@link com.earth2me.essentials.AlternativeCommandsHandler} object.
     */
    AlternativeCommandsHandler getAlternativeCommandsHandler();

    /**
     * <p>showError.</p>
     *
     * @param sender a {@link com.earth2me.essentials.CommandSource} object.
     * @param exception a {@link java.lang.Throwable} object.
     * @param commandLabel a {@link java.lang.String} object.
     */
    void showError(CommandSource sender, Throwable exception, String commandLabel);

    /**
     * <p>getItemDb.</p>
     *
     * @return a {@link com.earth2me.essentials.api.IItemDb} object.
     */
    IItemDb getItemDb();

    /**
     * <p>getUserMap.</p>
     *
     * @return a {@link com.earth2me.essentials.UserMap} object.
     */
    UserMap getUserMap();

    /**
     * <p>getMetrics.</p>
     *
     * @return a {@link com.earth2me.essentials.metrics.Metrics} object.
     */
    Metrics getMetrics();

    /**
     * <p>setMetrics.</p>
     *
     * @param metrics a {@link com.earth2me.essentials.metrics.Metrics} object.
     */
    void setMetrics(Metrics metrics);

    /**
     * <p>getTimer.</p>
     *
     * @return a {@link com.earth2me.essentials.EssentialsTimer} object.
     */
    EssentialsTimer getTimer();

    /**
     * <p>getVanishedPlayers.</p>
     *
     * @return a {@link java.util.List} object.
     */
    @Deprecated
    List<String> getVanishedPlayers();

    /**
     * <p>getOnlinePlayers.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<Player> getOnlinePlayers();

    /**
     * <p>getOnlineUsers.</p>
     *
     * @return a {@link java.lang.Iterable} object.
     */
    Iterable<User> getOnlineUsers();

    /**
     * <p>getSpawnerProvider.</p>
     *
     * @return a {@link net.ess3.nms.SpawnerProvider} object.
     */
    SpawnerProvider getSpawnerProvider();
}
