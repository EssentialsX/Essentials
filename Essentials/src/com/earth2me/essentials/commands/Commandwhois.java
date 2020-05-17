package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.BanLookup;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.BanEntry;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Statistic;

import java.util.*;

import static com.earth2me.essentials.I18n.tl;


public class Commandwhois extends EssentialsCommand {
    // For some reason, in 1.13 PLAY_ONE_MINUTE = ticks played = what used to be PLAY_ONE_TICK
    // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/commits/b848d8ce633871b52115247b089029749c02f579
    private static final Statistic PLAY_ONE_TICK = EnumUtil.getStatistic("PLAY_ONE_MINUTE", "PLAY_ONE_TICK");

    public Commandwhois() {
        super("whois");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        User player;
        try {
            final UUID uuid = UUID.fromString(args[0]);
            player = ess.getUser(uuid);
        } catch (IllegalArgumentException ignored) {
            player = ess.getOfflineUser(args[0]);
        }
        if (player == null) {
            ess.getScheduler().runTaskAsynchronously(ess, () -> {
                User userFromBukkit = ess.getUserMap().getUserFromBukkit(args[0]);
                try {
                    if (userFromBukkit != null) {
                        sendWhois(sender, userFromBukkit);
                    } else {
                        User target;
                        if (sender.isPlayer()) {
                            User senderPlayer = ess.getUser(sender.getPlayer());
                            target = getPlayer(server, senderPlayer, args, 0, senderPlayer.canInteractVanished(), true);
                        } else {
                            target = getPlayer(server, args, 0, true, true);
                        }
                        sendWhois(sender, target);
                    }
                } catch (Exception e) {
                    ess.showError(sender, e, commandLabel);
                }
            });
        }
        else {
            sendWhois(sender, player);
        }
    }

    private void sendWhois(final CommandSource sender, final User user) {
        final boolean online = user.getBase().isOnline();
        sender.sendMessage(tl("whoisTop", user.getName()));
        user.setDisplayNick();
        sender.sendMessage(tl("whoisNick", user.getDisplayName()));
        sender.sendMessage(tl("whoisUuid", user.getBase().getUniqueId().toString()));
        if (online) {
            sender.sendMessage(tl("whoisOnline"));
        } else {
            sender.sendMessage(tl("whoisOffline"));
        }
        if (online) {
            sender.sendMessage(tl("whoisHealth", user.getBase().getHealth()));
            sender.sendMessage(tl("whoisHunger", user.getBase().getFoodLevel(), user.getBase().getSaturation()));
            sender.sendMessage(tl("whoisExp", SetExpFix.getTotalExperience(user.getBase()), user.getBase().getLevel()));
        }
        final Location loc = online ? user.getLocation() : user.getLogoutLocation();
        sender.sendMessage(tl("whoisLocation", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        if (online) {
            long playtimeMs = System.currentTimeMillis() - (user.getBase().getStatistic(PLAY_ONE_TICK) * 50);
            sender.sendMessage(tl("whoisPlaytime", DateUtil.formatDateDiff(playtimeMs)));
        }
        if (!ess.getSettings().isEcoDisabled()) {
            sender.sendMessage(tl("whoisMoney", NumberUtil.displayCurrency(user.getMoney(), ess)));
        }
        if (!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials.whois.ip")) {
            final String ip = online ? user.getBase().getAddress().getAddress().toString() : user.getLastLoginAddress();
            sender.sendMessage(tl("whoisIPAddress", ip));
        }
        final String location = user.getGeoLocation();
        if (location != null && (!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show"))) {
            sender.sendMessage(tl("whoisGeoLocation", location));
        }
        if (online) {
            sender.sendMessage(tl("whoisGamemode", tl(user.getBase().getGameMode().toString().toLowerCase(Locale.ENGLISH))));
        }
        sender.sendMessage(tl("whoisGod", (user.isGodModeEnabled() ? tl("true") : tl("false"))));
        if (online) {
            sender.sendMessage(tl("whoisOp", (user.getBase().isOp() ? tl("true") : tl("false"))));
            sender.sendMessage(tl("whoisFly", user.getBase().getAllowFlight() ? tl("true") : tl("false"), user.getBase().isFlying() ? tl("flying") : tl("notFlying")));
            sender.sendMessage(tl("whoisSpeed", user.getBase().isFlying() ? user.getBase().getFlySpeed() : user.getBase().getWalkSpeed()));
            if (user.isAfk()) {
                sender.sendMessage(tl("whoisAFKSince", tl("true"), DateUtil.formatDateDiff(user.getAfkSince())));
            } else {
                sender.sendMessage(tl("whoisAFK", tl("false")));
            }
        }
        sender.sendMessage(tl("whoisJail", (user.isJailed() ? user.getJailTimeout() > 0 ? DateUtil.formatDateDiff(user.getJailTimeout()) : tl("true") : tl("false"))));

        long muteTimeout = user.getMuteTimeout();
        if (!user.hasMuteReason()) {
            sender.sendMessage(tl("whoisMuted", (user.isMuted() ? (muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : tl("true")) : tl("false"))));
        } else {
            sender.sendMessage(tl("whoisMutedReason", (user.isMuted() ? (muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : tl("true")) : tl("false")),
                user.getMuteReason()));
        }

        final BanEntry userBan = BanLookup.getBanEntry(ess, user.getName());
        if (userBan != null) {
            final String reason = userBan.getReason();
            sender.sendMessage(tl("whoisBanned", reason));
            if (userBan.getExpiration() != null) {
                Date expiry = userBan.getExpiration();
                String expireString = tl("now");
                if (expiry.after(new Date())) {
                    expireString = DateUtil.formatDateDiff(expiry.getTime());
                }
                sender.sendMessage(tl("whoisTempBanned", expireString));
            }
        }

        final BanEntry ipBan = BanLookup.getIpBanEntry(ess, user.getLastLoginAddress());
        if (ipBan != null) {
            final String reason = ipBan.getReason();
            sender.sendMessage(tl("whoisBannedIp", reason));
            if (ipBan.getExpiration() != null) {
                Date expiry = ipBan.getExpiration();
                String expireString = tl("now");
                if (expiry.after(new Date())) {
                    expireString = DateUtil.formatDateDiff(expiry.getTime());
                }
                sender.sendMessage(tl("whoisTempBannedIp", expireString));
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
