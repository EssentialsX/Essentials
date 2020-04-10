package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
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
        CompletableFuture<Exception> eFuture = new CompletableFuture<>();
        eFuture.thenAccept(e -> showError(user.getBase(), e, commandLabel));
        try {
            if ("bed".equalsIgnoreCase(homeName) && user.isAuthorized("essentials.home.bed")) {
                final Location bed = player.getBase().getBedSpawnLocation();
                if (bed != null) {
                    user.getAsyncTeleport().teleport(bed, charge, TeleportCause.COMMAND, eFuture, new CompletableFuture<>());
                    return;
                } else {
                    throw new Exception(tl("bedMissing"));
                }
            }
            goHome(user, player, homeName.toLowerCase(Locale.ENGLISH), charge, eFuture);
        } catch (NotEnoughArgumentsException e) {
            Location bed = player.getBase().getBedSpawnLocation();
            final List<String> homes = player.getHomes();
            if (homes.isEmpty() && player.equals(user)) {
                if (ess.getSettings().isSpawnIfNoHome()) {
                    user.getAsyncTeleport().respawn(charge, TeleportCause.COMMAND, eFuture, new CompletableFuture<>());
                } else {
                    throw new Exception(tl("noHomeSetPlayer"));
                }
            } else if (homes.isEmpty()) {
                throw new Exception(tl("noHomeSetPlayer"));
            } else if (homes.size() == 1 && player.equals(user)) {
                goHome(user, player, homes.get(0), charge, eFuture);
            } else {
                final int count = homes.size();
                if (user.isAuthorized("essentials.home.bed")) {
                    if (bed != null) {
                        homes.add(tl("bed"));
                    } else {
                        homes.add(tl("bedNull"));
                    }
                }
                user.sendMessage(tl("homes", StringUtil.joinList(homes), count, getHomeLimit(player)));
            }
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

    private void goHome(final User user, final User player, final String home, final Trade charge, CompletableFuture<Exception> eFuture) throws Exception {
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
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.thenAccept(success -> {
           if (success) {
               user.sendMessage(tl("teleportHome", home));
           }
        });
        user.getAsyncTeleport().teleport(loc, charge, TeleportCause.COMMAND, eFuture, future);
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
