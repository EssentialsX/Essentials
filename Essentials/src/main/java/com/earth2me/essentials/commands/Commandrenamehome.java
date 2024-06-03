package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.IUser;
import net.ess3.api.TranslatableException;
import net.essentialsx.api.v2.events.HomeModifyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Commandrenamehome extends EssentialsCommand {
    public Commandrenamehome() {
        super("renamehome");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        User usersHome = user;
        final String oldName;
        final String newName;

        // Allowing both formats /renamehome jroy home1 home | /sethome jroy:home1 home
        if (args.length == 2) {
            final String[] nameParts = args[0].split(":", 2);
            newName = args[1].toLowerCase(Locale.ENGLISH);

            if (nameParts.length == 2) {
                oldName = nameParts[1].toLowerCase(Locale.ENGLISH);
                if (user.isAuthorized("essentials.renamehome.others")) {
                    usersHome = getPlayer(server, nameParts[0], true, true);
                    if (usersHome == null) {
                        throw new PlayerNotFoundException();
                    }
                }
            } else {
                oldName = args[0].toLowerCase(Locale.ENGLISH);
            }
        } else if (args.length == 3) {
            if (!user.isAuthorized("essentials.renamehome.others")) {
                throw new NotEnoughArgumentsException();
            }

            usersHome = getPlayer(server, args[0], true, true);
            if (usersHome == null) {
                throw new PlayerNotFoundException();
            }

            oldName = args[1].toLowerCase(Locale.ENGLISH);
            newName = args[2].toLowerCase(Locale.ENGLISH);
        } else {
            throw new NotEnoughArgumentsException();
        }

        if ("bed".equals(newName) || NumberUtil.isInt(newName) || "bed".equals(oldName) || NumberUtil.isInt(oldName)) {
            throw new TranslatableException("invalidHomeName");
        }

        final HomeModifyEvent event = new HomeModifyEvent(user, usersHome, oldName, newName, usersHome.getHome(oldName));
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info("HomeModifyEvent canceled for /renamehome execution by " + user.getDisplayName());
            }
            return;
        }

        usersHome.renameHome(oldName, newName);
        user.sendTl("homeRenamed", oldName, newName);
        usersHome.setLastHomeConfirmation(null);

    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        final IUser user = sender.getUser();
        if (args.length != 1) {
            return Collections.emptyList();
        }

        final List<String> homes = user == null ? new ArrayList<>() : user.getHomes();
        final boolean canRenameOthers = sender.isAuthorized("essentials.renamehome.others");

        if (canRenameOthers) {
            final int sepIndex = args[0].indexOf(':');
            if (sepIndex < 0) {
                getPlayers(server, sender).forEach(player -> homes.add(player + ":"));
            } else {
                final String namePart = args[0].substring(0, sepIndex);
                final User otherUser;
                try {
                    otherUser = getPlayer(server, new String[]{namePart}, 0, true, true);
                } catch (final Exception ex) {
                    return homes;
                }
                otherUser.getHomes().forEach(home -> homes.add(namePart + ":" + home));
            }
        }
        return homes;
    }
}
