package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.essentialsx.api.v2.events.UserActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.earth2me.essentials.I18n.tl;

public class Commandme extends EssentialsCommand {
    public Commandme() {
        super("me");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (user.isMuted()) {
            final String dateDiff = user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : null;
            if (dateDiff == null) {
                throw new Exception(user.hasMuteReason() ? tl("voiceSilencedReason", user.getMuteReason()) : tl("voiceSilenced"));
            }
            throw new Exception(user.hasMuteReason() ? tl("voiceSilencedReasonTime", dateDiff, user.getMuteReason()) : tl("voiceSilencedTime", dateDiff));
        }

        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        message = FormatUtil.formatMessage(user, "essentials.chat", message);

        user.setDisplayNick();
        long radius = ess.getSettings().getChatRadius();
        final String toSend = tl("action", user.getName(), message);
        if (radius < 1) {
            ess.broadcastMessage(user, toSend);
            ess.getServer().getPluginManager().callEvent(new UserActionEvent(user, message, Collections.unmodifiableCollection(ess.getServer().getOnlinePlayers())));
            return;
        }
        radius *= radius;

        final World world = user.getWorld();
        final Location loc = user.getLocation();
        final Set<Player> outList = new HashSet<>();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            final User onlineUser = ess.getUser(player);
            if (!onlineUser.equals(user)) {
                boolean abort = false;
                final Location playerLoc = onlineUser.getLocation();
                if (playerLoc.getWorld() != world) {
                    abort = true;
                } else if (onlineUser.isIgnoredPlayer(user)) {
                    abort = true;
                } else {
                    final double delta = playerLoc.distanceSquared(loc);
                    if (delta > radius) {
                        abort = true;
                    }
                }
                if (abort) {
                    if (onlineUser.isAuthorized("essentials.chat.spy")) {
                        outList.add(player); // Just use the same list unless we wanted to format spyying for this.
                    }
                } else {
                    outList.add(player);
                }
            } else {
                outList.add(player); // Add yourself to the list.
            }
        }

        if (outList.size() < 2) {
            user.sendMessage(tl("localNoOne"));
        }

        for (final Player onlinePlayer : outList) {
            onlinePlayer.sendMessage(toSend);
        }
        ess.getServer().getPluginManager().callEvent(new UserActionEvent(user, message, Collections.unmodifiableCollection(outList)));
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        message = FormatUtil.replaceFormat(message);

        ess.getServer().broadcastMessage(tl("action", "@", message));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        return Collections.emptyList();
    }
}
