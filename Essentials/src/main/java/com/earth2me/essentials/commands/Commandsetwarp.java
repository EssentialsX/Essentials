package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.api.IWarps;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import net.ess3.api.InvalidWorldException;
import net.essentialsx.api.v2.events.WarpModifyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandsetwarp extends EssentialsCommand {
    public Commandsetwarp() {
        super("setwarp");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        if (NumberUtil.isInt(args[0]) || args[0].isEmpty()) {
            throw new Exception(tl("invalidWarpName"));
        }

        final IWarps warps = ess.getWarps();
        Location warpLoc = null;

        try {
            warpLoc = warps.getWarp(args[0]);
        } catch (final WarpNotFoundException | InvalidWorldException ignored) {
        }
        if (warpLoc == null) {
            final WarpModifyEvent event = new WarpModifyEvent(user, args[0], null, user.getLocation(), WarpModifyEvent.WarpModifyCause.CREATE);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            warps.setWarp(user, args[0], user.getLocation());
        } else if (user.isAuthorized("essentials.warp.overwrite." + StringUtil.safeString(args[0]))) {
            final WarpModifyEvent event = new WarpModifyEvent(user, args[0], warpLoc, user.getLocation(), WarpModifyEvent.WarpModifyCause.UPDATE);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            warps.setWarp(user, args[0], user.getLocation());
        } else {
            throw new Exception(tl("warpOverwrite"));
        }
        user.sendMessage(tl("warpSet", args[0]));
    }
}
