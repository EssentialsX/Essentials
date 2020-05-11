package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static com.earth2me.essentials.I18n.tl;


public class Commandhome extends EssentialsCommand {
    public Commandhome() {
        super("home");
    }

    // This method contains an undocumented translation parameters #EasterEgg
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final Trade charge = new Trade(this.getName(), ess);
        User player = user;
        String homeName = "";
        String[] nameParts;
        if (args.length > 0) {
            nameParts = args[0].split(":");
            if (nameParts[0].length() == args[0].length() || !user.isAuthorized("essentials.home.others")) {
                homeName = nameParts[0];
            } else {
                player = getPlayer(server, nameParts, 0, true, true);
                if (nameParts.length > 1) {
                    homeName = nameParts[1];
                }
            }
        }
        try {
            if ("bed".equalsIgnoreCase(homeName) && user.isAuthorized("essentials.home.bed")) {
                PaperLib.getBedSpawnLocationAsync(player.getBase(), true).thenAccept(location -> {
                    if (location != null) {
                        user.getAsyncTeleport().teleport(location, charge, TeleportCause.COMMAND, getNewExceptionFuture(user.getSource(), commandLabel));
                    } else {
                        showError(user.getBase(), new Exception(tl("bedMissing")), commandLabel);
                    }
                });
                return;
            }
            goHome(user, player, homeName.toLowerCase(Locale.ENGLISH), charge, getNewExceptionFuture(user.getSource(), commandLabel));
        } catch (NotEnoughArgumentsException e) {
            final User finalPlayer = player;
            PaperLib.getBedSpawnLocationAsync(player.getBase(), true).thenAccept(bed -> {
                final List<String> homes = finalPlayer.getHomes();
                if (homes.isEmpty() && finalPlayer.equals(user)) {
                    if (ess.getSettings().isSpawnIfNoHome()) {
                        user.getAsyncTeleport().respawn(charge, TeleportCause.COMMAND, getNewExceptionFuture(user.getSource(), commandLabel));
                    } else {
                        showError(user.getBase(), new Exception(tl("noHomeSetPlayer")), commandLabel);
                    }
                } else if (homes.isEmpty()) {
                    showError(user.getBase(), new Exception(tl("noHomeSetPlayer")), commandLabel);
                } else if (homes.size() == 1 && finalPlayer.equals(user)) {
                    try {
                        goHome(user, finalPlayer, homes.get(0), charge, getNewExceptionFuture(user.getSource(), commandLabel));
                    } catch (Exception exception) {
                        showError(user.getBase(), exception, commandLabel);
                    }
                } else {
                    final int count = homes.size();
                    if (user.isAuthorized("essentials.home.bed")) {
                        if (bed != null) {
                            homes.add(tl("bed"));
                        } else {
                            homes.add(tl("bedNull"));
                        }
                    }
                    user.sendMessage(tl("homes", StringUtil.joinList(homes), count, getHomeLimit(finalPlayer)));
                }
            });
        }
    }

    private String getHomeLimit(final User player) {
        if (!player.getBase().isOnline()) {
            return "?";
        }
        if (player.isAuthorized("essentials.sethome.multiple.unlimited")) {
            return "*";
        }
        return Integer.toString(ess.getSettings().getHomeLimit(player));
    }

    private void goHome(final User user, final User player, final String home, final Trade charge, CompletableFuture<Boolean> future) throws Exception {
        if (home.length() < 1) {
            throw new NotEnoughArgumentsException();
        }
        final Location loc = player.getHome(home);
        if (loc == null) {
            throw new NotEnoughArgumentsException();
        }
        if (user.getWorld() != loc.getWorld() && ess.getSettings().isWorldHomePermissions() && !user.isAuthorized("essentials.worlds." + loc.getWorld().getName())) {
            throw new Exception(tl("noPerm", "essentials.worlds." + loc.getWorld().getName()));
        }
        user.getAsyncTeleport().teleport(loc, charge, TeleportCause.COMMAND, future);
        future.thenAccept(success -> {
            if (success) {
                user.sendMessage(tl("teleportHome", home));
            }
        });
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        boolean canVisitOthers = user.isAuthorized("essentials.home.others");

        if (args.length == 1) {
            if (canVisitOthers) {
                return getPlayers(server, user);
            } else {
                List<String> homes = user.getHomes();
                if (user.isAuthorized("essentials.home.bed")) {
                    homes.add("bed");
                }
                return homes;
            }
        } else if (args.length == 2 && canVisitOthers) {
            try {
                User otherUser = getPlayer(server, args, 0, true, true);
                List<String> homes = otherUser.getHomes();
                if (user.isAuthorized("essentials.home.bed")) {
                    homes.add("bed");
                }
                return homes;
            } catch (Exception ex) {
                // No such user
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }
}
