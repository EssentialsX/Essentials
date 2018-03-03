package com.neximation.essentials.commands;

import com.neximation.essentials.User;
import com.neximation.essentials.api.IWarps;
import com.neximation.essentials.utils.NumberUtil;
import com.neximation.essentials.utils.StringUtil;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;
import org.bukkit.Server;

import static com.neximation.essentials.I18n.tl;


public class Commandsetwarp extends EssentialsCommand {
    public Commandsetwarp() {
        super("setwarp");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        if (NumberUtil.isInt(args[0]) || args[0].isEmpty()) {
            throw new NoSuchFieldException(tl("invalidWarpName"));
        }

        final Location loc = user.getLocation();
        final IWarps warps = ess.getWarps();
        Location warpLoc = null;

        try {
            warpLoc = warps.getWarp(args[0]);
        } catch (WarpNotFoundException | InvalidWorldException ex) {
        }

        if (warpLoc == null || user.isAuthorized("essentials.warp.overwrite." + StringUtil.safeString(args[0]))) {
            warps.setWarp(args[0], loc);
        } else {
            throw new Exception(tl("warpOverwrite"));
        }
        user.sendMessage(tl("warpSet", args[0]));
    }
}
