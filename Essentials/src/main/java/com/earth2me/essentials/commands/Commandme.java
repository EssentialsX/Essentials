package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.TranslatableException;
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

import static com.earth2me.essentials.I18n.tlLiteral;

public class Commandme extends EssentialsCommand {
    public Commandme() {
        super("me");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (user.isMuted()) {
            final String dateDiff = user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : null;
            if (dateDiff == null) {
                throw new TranslatableException(user.hasMuteReason() ? "voiceSilencedReason" : "voiceSilenced", user.getMuteReason());
            }
            throw new TranslatableException(user.hasMuteReason() ? "voiceSilencedReasonTime" : "voiceSilencedTime", dateDiff, user.getMuteReason());
        }

        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        message = FormatUtil.formatMessage(user, "essentials.chat", message);

        user.setDisplayNick();
        long radius = ess.getSettings().getChatRadius();
        if (radius < 1) {
            ess.broadcastTl("action", user.getDisplayName(), message);
            ess.getServer().getPluginManager().callEvent(new UserActionEvent(user, message, Collections.unmodifiableCollection(ess.getServer().getOnlinePlayers())));
            return;
        }
        radius *= radius;

        final World world = user.getWorld();
        final Location loc = user.getLocation();
        final Set<User> outList = new HashSet<>();

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
                        outList.add(onlineUser); // Just use the same list unless we wanted to format spyying for this.
                    }
                } else {
                    outList.add(onlineUser);
                }
            } else {
                outList.add(onlineUser); // Add yourself to the list.
            }
        }

        if (outList.size() < 2) {
            user.sendTl("localNoOne");
        }

        for (final User onlineUser : outList) {
            onlineUser.sendTl("action", user.getDisplayName(), message);
        }

        // Only take the time to generate this list if there are listeners.
        if (UserActionEvent.getHandlerList().getRegisteredListeners().length > 0) {
            final Set<Player> outListPlayers = new HashSet<>();
            for (final User onlineUser : outList) {
                outListPlayers.add(onlineUser.getBase());
            }

            ess.getServer().getPluginManager().callEvent(new UserActionEvent(user, message, Collections.unmodifiableCollection(outListPlayers)));
        }
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        message = FormatUtil.replaceFormat(message);

        ess.getServer().broadcastMessage(tlLiteral("action", "@", message));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        return Collections.emptyList();
    }
}
