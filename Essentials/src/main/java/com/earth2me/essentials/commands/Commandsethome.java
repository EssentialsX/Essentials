package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.NumberUtil;
import net.essentialsx.api.v2.events.HomeModifyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.earth2me.essentials.I18n.tl;

public class Commandsethome extends EssentialsCommand {
    public Commandsethome() {
        super("sethome");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, String[] args) throws Exception {
        User usersHome = user;
        String name = "home";

        if (args.length > 0) {
            //Allowing both formats /sethome khobbits house | /sethome khobbits:house
            final String[] nameParts = args[0].split(":");
            if (nameParts[0].length() != args[0].length()) {
                args = nameParts;
            }

            if (args.length < 2) {
                name = args[0].toLowerCase(Locale.ENGLISH);
            } else {
                name = args[1].toLowerCase(Locale.ENGLISH);
                if (user.isAuthorized("essentials.sethome.others")) {
                    usersHome = getPlayer(server, args[0], true, true);
                    if (usersHome == null) {
                        throw new PlayerNotFoundException();
                    }
                }
            }
        }
        if (checkHomeLimit(user, usersHome, name)) {
            name = "home";
        }
        if ("bed".equals(name) || NumberUtil.isInt(name)) {
            throw new NoSuchFieldException(tl("invalidHomeName"));
        }

        final Location location = user.getLocation();
        if ((!ess.getSettings().isTeleportSafetyEnabled() || !ess.getSettings().isForceDisableTeleportSafety()) && LocationUtil.isBlockUnsafeForUser(ess, usersHome, location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
            throw new Exception(tl("unsafeTeleportDestination", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }

        if (ess.getSettings().isConfirmHomeOverwrite() && usersHome.hasHome(name) && (!name.equals(usersHome.getLastHomeConfirmation()) || name.equals(usersHome.getLastHomeConfirmation()) && System.currentTimeMillis() - usersHome.getLastHomeConfirmationTimestamp() > TimeUnit.MINUTES.toMillis(2))) {
            usersHome.setLastHomeConfirmation(name);
            usersHome.setLastHomeConfirmationTimestamp();
            user.sendMessage(tl("homeConfirmation", name));
            return;
        }

        final Location prevHomeLoc = usersHome.getHome(name);

        final HomeModifyEvent event;
        if (prevHomeLoc == null) {
            event = new HomeModifyEvent(user, usersHome, name, location, true);
        } else {
            event = new HomeModifyEvent(user, usersHome, name, prevHomeLoc, location);
        }

        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info("HomeModifyEvent canceled for /sethome execution by " + user.getDisplayName());
            }
            return;
        }

        usersHome.setHome(name, location);
        user.sendMessage(tl("homeSet", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), name));
        usersHome.setLastHomeConfirmation(null);

    }

    private boolean checkHomeLimit(final User user, final User usersHome, final String name) throws Exception {
        if (!user.isAuthorized("essentials.sethome.multiple.unlimited")) {
            final int limit = ess.getSettings().getHomeLimit(user);
            final List<String> homes = usersHome.isReachable() ?
                    usersHome.getHomesPerWorld(Objects.requireNonNull(usersHome.getLocation().getWorld()).getName())
                    : usersHome.getHomes();
            if (homes.size() >= limit) {
                if (usersHome.getHomes().contains(name)) {
                    return false;
                }
                throw new Exception(tl("maxHomes", ess.getSettings().getHomeLimit(user)));
            }
            return limit == 1;
        }
        return false;
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        return Collections.emptyList();
    }
}
