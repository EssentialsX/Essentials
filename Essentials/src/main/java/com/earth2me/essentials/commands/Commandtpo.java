package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.earth2me.essentials.I18n.tl;

public class Commandtpo extends EssentialsCommand {
    public Commandtpo() {
        super("tpo");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        switch (args.length) {
            case 0:
                throw new NotEnoughArgumentsException();

            case 1:
                final User player = getPlayer(server, user, args, 0);
                if (user.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + player.getWorld().getName())) {
                    throw new Exception(tl("noPerm", "essentials.worlds." + player.getWorld().getName()));
                }
                user.getAsyncTeleport().now(player.getBase(), false, TeleportCause.COMMAND, getNewExceptionFuture(user.getSource(), commandLabel));
                break;

            default:
                if (!user.isAuthorized("essentials.tp.others")) {
                    throw new Exception(tl("noPerm", "essentials.tp.others"));
                }
                final User target = getPlayer(server, user, args, 0);
                final User toPlayer = getPlayer(server, user, args, 1);

                if (target.getWorld() != toPlayer.getWorld() && ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + toPlayer.getWorld().getName())) {
                    throw new Exception(tl("noPerm", "essentials.worlds." + toPlayer.getWorld().getName()));
                }

                final CompletableFuture<Boolean> future = getNewExceptionFuture(user.getSource(), commandLabel);
                target.getAsyncTeleport().now(toPlayer.getBase(), false, TeleportCause.COMMAND, future);
                future.thenAccept(success -> {
                    if (success) {
                        target.sendMessage(tl("teleportAtoB", user.getName(), toPlayer.getName()));
                    }
                });
                break;
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        // Don't handle coords
        if (args.length == 1 || (args.length == 2 && user.isAuthorized("essentials.tp.others"))) {
            return getPlayers(server, user);
        } else {
            return Collections.emptyList();
        }
    }
}
