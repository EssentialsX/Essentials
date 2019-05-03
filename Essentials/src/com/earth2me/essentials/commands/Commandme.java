package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.earth2me.essentials.I18n.tl;


public class Commandme extends EssentialsCommand {
    public Commandme() {
        super("me");
    }

    @Override
    public void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        if (user.isMuted()) {
            if (user.getMuteReason ().equals ("")) {
                throw new Exception(tl("voiceSilenced"));
            }
            else {
                throw new Exception(tl("voiceSilenced") + tl("muteFormat", user.getMuteReason ()));
            }
        }

        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        message = FormatUtil.formatMessage(user, "essentials.chat", message);

        user.setDisplayNick();
        int radius = ess.getSettings().getChatRadius();
        String toSend = tl("action", user.getDisplayName(), message);
        if (radius < 1) {
            ess.broadcastMessage(user, toSend);
            return;
        }

        World world = user.getWorld();
        Location loc = user.getLocation();
        Set<Player> outList = new HashSet<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
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

        for (Player onlinePlayer : outList) {
            onlinePlayer.sendMessage(toSend);
        }
    }

    @Override
    public void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        message = FormatUtil.replaceFormat(message);

        ess.getServer().broadcastMessage(tl("action", "@", message));
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        return null;  // It's a chat message, use the default chat handler
    }
}
