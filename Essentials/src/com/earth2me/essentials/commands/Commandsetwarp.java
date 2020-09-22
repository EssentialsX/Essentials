package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.api.IWarps;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandsetwarp extends EssentialsCommand {
    public Commandsetwarp() {
        super("setwarp");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args)
            throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        final String warpName = args[0];
        final String safeWarpName = StringUtil.safeString(warpName);

        if (NumberUtil.isInt(warpName) || warpName.isEmpty()) {
            throw new Exception(tl("invalidWarpName"));
        }

        final IWarps warps = ess.getWarps();
        Location warpLoc = null;

        try {
            warpLoc = warps.getWarp(warpName);
        } catch (WarpNotFoundException | InvalidWorldException ignored) {
        }

        final boolean warpExists = warpLoc != null;
        final boolean canOverwrite = user.isAuthorized("essentials.warp.overwrite." + safeWarpName);
        final boolean canCreate = user.isAuthorized("essentials.warp.set." + safeWarpName);

        if (warpExists && !canOverwrite) {
            throw new Exception(tl("warpOverwrite"));
        }

        if (!warpExists && !canCreate && !canOverwrite) {
            throw new Exception(tl("warpCreate"));
        }

        warps.setWarp(user, warpName, user.getLocation());
        user.sendMessage(tl("warpSet", warpName));
    }
}
