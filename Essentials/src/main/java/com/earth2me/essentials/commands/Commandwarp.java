package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.api.IWarps;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import net.ess3.api.IUser;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;

public class Commandwarp extends EssentialsCommand {
    private static final int WARPS_PER_PAGE = 20;

    public Commandwarp() {
        super("warp");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0 || args[0].matches("[0-9]+")) {
            if (!user.isAuthorized("essentials.warp.list")) {
                throw new Exception(tl("warpListPermission"));
            }
            warpList(user.getSource(), args, user);
            throw new NoChargeException();
        }

        //TODO: Remove 'otherplayers' permission.
        if (args.length == 2 && (user.isAuthorized("essentials.warp.otherplayers") || user.isAuthorized("essentials.warp.others"))) {
            final User otherUser = getPlayer(server, user, args, 1);
            warpUser(user, otherUser, args[0], commandLabel);
            throw new NoChargeException();
        }
        warpUser(user, user, args[0], commandLabel);
        throw new NoChargeException();
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2 || NumberUtil.isInt(args[0])) {
            warpList(sender, args, null);
            throw new NoChargeException();
        }
        final User otherUser = getPlayer(server, args, 1, true, false);
        otherUser.getAsyncTeleport().warp(otherUser, args[0], null, TeleportCause.COMMAND, getNewExceptionFuture(sender, commandLabel));
        throw new NoChargeException();
    }

    //TODO: Use one of the new text classes, like /help ?
    private void warpList(final CommandSource sender, final String[] args, final IUser user) throws Exception {
        final IWarps warps = ess.getWarps();
        final List<String> warpNameList = getAvailableWarpsFor(user);

        if (warpNameList.isEmpty()) {
            throw new Exception(tl("noWarpsDefined"));
        }
        int page = 1;
        if (args.length > 0 && NumberUtil.isInt(args[0])) {
            page = Integer.parseInt(args[0]);
        }

        final int maxPages = (int) Math.ceil(warpNameList.size() / (double) WARPS_PER_PAGE);

        if (page > maxPages) {
            page = maxPages;
        }

        final int warpPage = (page - 1) * WARPS_PER_PAGE;
        final String warpList = StringUtil.joinList(warpNameList.subList(warpPage, warpPage + Math.min(warpNameList.size() - warpPage, WARPS_PER_PAGE)));

        if (warpNameList.size() > WARPS_PER_PAGE) {
            sender.sendMessage(tl("warpsCount", warpNameList.size(), page, maxPages));
            sender.sendMessage(tl("warpList", warpList));
        } else {
            sender.sendMessage(tl("warps", warpList));
        }
    }

    private void warpUser(final User owner, final User user, final String name, final String commandLabel) throws Exception {
        final Trade chargeWarp = new Trade("warp-" + name.toLowerCase(Locale.ENGLISH).replace('_', '-'), ess);
        final Trade chargeCmd = new Trade(this.getName(), ess);
        final BigDecimal fullCharge = chargeWarp.getCommandCost(user).add(chargeCmd.getCommandCost(user));
        final Trade charge = new Trade(fullCharge, ess);
        charge.isAffordableFor(owner);
        if (ess.getSettings().getPerWarpPermission() && !owner.isAuthorized("essentials.warps." + name)) {
            throw new Exception(tl("warpUsePermission"));
        }
        owner.getAsyncTeleport().warp(user, name, charge, TeleportCause.COMMAND, getNewExceptionFuture(user.getSource(), commandLabel));
    }

    private List<String> getAvailableWarpsFor(final IUser user) {
        if (ess.getSettings().getPerWarpPermission() && user != null) {
            return ess.getWarps().getList().stream()
                .filter(warpName -> user.isAuthorized("essentials.warps." + warpName))
                .collect(Collectors.toList());
        }

        return new ArrayList<>(ess.getWarps().getList());
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1 && user.isAuthorized("essentials.warp.list")) {
            return getAvailableWarpsFor(user);
        } else if (args.length == 2 && (user.isAuthorized("essentials.warp.otherplayers") || user.isAuthorized("essentials.warp.others"))) {
            //TODO: Remove 'otherplayers' permission.
            return getPlayers(server, user);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(ess.getWarps().getList());
        } else if (args.length == 2) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
