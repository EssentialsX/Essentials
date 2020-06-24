package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;


public class Commandback extends EssentialsCommand {
    public Commandback() {
        super("back");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        CommandSource sender = user.getSource();
        if (args.length > 0 && user.isAuthorized("essentials.back.others")) {
            this.parseCommand(server, sender, args, true, commandLabel);
            return;
        }

        teleportBack(sender, user, commandLabel);
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        this.parseCommand(server, sender, args, true, commandLabel);
    }

    private void parseCommand(Server server, CommandSource sender, String[] args, boolean allowOthers, String commandLabel) throws Exception {
        Collection<Player> players = new ArrayList<>();

        if (allowOthers && args.length > 0 && args[0].trim().length() > 2) {
            players = server.matchPlayer(args[0].trim());
        }

        if (players.size() < 1) {
            throw new PlayerNotFoundException();
        }

        for (Player player : players) {
            sender.sendMessage(tl("backOther", player.getName()));
            teleportBack(sender, ess.getUser(player), commandLabel);
        }
    }

    private void teleportBack(CommandSource sender, User user, String commandLabel) throws Exception {
        if (user.getLastLocation() == null) {
            throw new Exception(tl("noLocationFound"));
        }

        String lastWorldName = user.getLastLocation().getWorld().getName();

        User requester = null;
        if (sender.isPlayer()) {
            requester = ess.getUser(sender.getPlayer());

            if (user.getWorld() != user.getLastLocation().getWorld() && this.ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + lastWorldName)) {
                throw new Exception(tl("noPerm", "essentials.worlds." + lastWorldName));
            }

            if (!requester.isAuthorized("essentials.back.into." + lastWorldName)) {
                throw new Exception(tl("noPerm", "essentials.back.into." + lastWorldName));
            }
        }

        if (requester == null) {
            user.getAsyncTeleport().back(null, null, getNewExceptionFuture(sender, commandLabel));
        } else if (!requester.equals(user)) {
            Trade charge = new Trade(this.getName(), this.ess);
            charge.isAffordableFor(requester);
            user.getAsyncTeleport().back(requester, charge, getNewExceptionFuture(sender, commandLabel));
        } else {
            Trade charge = new Trade(this.getName(), this.ess);
            charge.isAffordableFor(user);
            user.getAsyncTeleport().back(charge, getNewExceptionFuture(sender, commandLabel));
        }
        throw new NoChargeException();
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (user.isAuthorized("essentials.back.others") && args.length == 1) {
            return getPlayers(server, user);
        } else {
            return Collections.emptyList();
        }
    }
}
