package com.earth2me.essentials.spawn;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;


public class Commandspawn extends EssentialsCommand {
    public Commandspawn() {
        super("spawn");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        if (args.length > 0 && user.isAuthorized("essentials.spawn.others")) {
            final User otherUser = getPlayer(server, user, args, 0);
            respawn(user.getSource(), user, otherUser, charge);
            if (!otherUser.equals(user)) {
                otherUser.sendMessage(tl("teleportAtoB", user.getDisplayName(), "spawn"));
            }
        } else {
            respawn(user.getSource(), user, user, charge);
        }
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }
        final User user = getPlayer(server, args, 0, true, false);
        respawn(sender, null, user, null);
        user.sendMessage(tl("teleportAtoB", Console.NAME, "spawn"));
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1 && sender.isAuthorized("essentials.spawn.others", ess)) {
            return getPlayers(server, sender);
        }
        return Collections.emptyList();
    }

    private void respawn(final CommandSource sender, final User teleportOwner, final User teleportee, final Trade charge) throws Exception {
        final Location spawn = ((SpawnStorage) this.module).getSpawn(teleportee.getGroup());
        sender.sendMessage(tl("teleporting", spawn.getWorld().getName(), spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()));
        if (teleportOwner == null) {
            teleportee.getTeleport().now(spawn, false, TeleportCause.COMMAND);
            return;
        }
        teleportOwner.getTeleport().teleportPlayer(teleportee, spawn, charge, TeleportCause.COMMAND);
    }
}
