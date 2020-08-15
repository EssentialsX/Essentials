package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import com.earth2me.essentials.craftbukkit.BanLookup;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Collections;

import static com.earth2me.essentials.I18n.tl;


public class Commandseen extends EssentialsCommand {
    public Commandseen() {
        super("seen");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        boolean showBan = sender.isAuthorized("essentials.seen.banreason", ess);
        boolean showIp = sender.isAuthorized("essentials.seen.ip", ess);
        boolean showLocation = sender.isAuthorized("essentials.seen.location", ess);
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        User player;
        // check by uuid
        try {
            UUID uuid = UUID.fromString(args[0]);
            player = ess.getUser(uuid);
        } catch (IllegalArgumentException ignored1) {
            // check by ip
            if (ipLookup && FormatUtil.validIP(args[0])) {
                seenIP(server, sender, args[0]);
                return;
            }
            // check if ip banned
            else if (ess.getServer().getBanList(BanList.Type.IP).isBanned(args[0])) {
                sender.sendMessage(tl("isIpBanned", args[0]));
                return;
            }
            // check if name banned
            else if (BanLookup.isBanned(ess, args[0])) {
                sender.sendMessage(tl("whoisBanned", showBan ? BanLookup.getBanEntry(ess, args[0]).getReason() : tl("true")));
                return;
            }
            // check by name
            try {
                player = getPlayer(server, args, 0, false, true);
            } catch (PlayerNotFoundException ignored2) {
                throw new Exception(tl("playerNeverOnServer", args[0]));
            }
        }

        if (player.getBase().isOnline() && canInteractWith(sender, player)) {
            seenOnline(sender, player, showIp);
        } else {
            seenOffline(sender, player, showBan, showIp, showLocation);
        }
    }

    private void seenOnline(final CommandSource sender, final User user, final boolean showIp) {

        user.setDisplayNick();
        sender.sendMessage(tl("seenOnline", user.getDisplayName(), DateUtil.formatDateDiff(user.getLastLogin())));

        if (ess.getSettings().isDebug()) {
            ess.getLogger().info("UUID: " + user.getBase().getUniqueId().toString());
        }

        List<String> history = ess.getUserMap().getUserHistory(user.getBase().getUniqueId());
        if (history != null && history.size() > 1) {
            sender.sendMessage(tl("seenAccounts", StringUtil.joinListSkip(", ", user.getName(), history)));
        }

        if (user.isAfk()) {
            sender.sendMessage(tl("whoisAFK", tl("true")));
        }
        if (user.isJailed()) {
            sender.sendMessage(tl("whoisJail", (user.getJailTimeout() > 0 ? DateUtil.formatDateDiff(user.getJailTimeout()) : tl("true"))));
        }
        if (user.isMuted()) {
            long muteTimeout = user.getMuteTimeout();
            if (!user.hasMuteReason()) {
                sender.sendMessage(tl("whoisMuted", (muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : tl("true"))));
            } else {
                sender.sendMessage(tl("whoisMutedReason", (muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : tl("true")), user.getMuteReason()));
            }
        }
        final String location = user.getGeoLocation();
        if (location != null && (!(sender.isPlayer()) || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show"))) {
            sender.sendMessage(tl("whoisGeoLocation", location));
        }
        if (showIp) {
            sender.sendMessage(tl("whoisIPAddress", user.getBase().getAddress().getAddress().toString()));
        }
    }

    private void seenOffline(final CommandSource sender, User user, final boolean showBan, final boolean showIp, final boolean showLocation) {
        user.setDisplayNick();
        if (user.getLastLogout() > 0) {
            sender.sendMessage(tl("seenOffline", user.getName(), DateUtil.formatDateDiff(user.getLastLogout())));
        } else {
            sender.sendMessage(tl("userUnknown", user.getName()));
        }

        if (ess.getSettings().isDebug()) {
            ess.getLogger().info("UUID: " + user.getBase().getUniqueId().toString());
        }

        List<String> history = ess.getUserMap().getUserHistory(user.getBase().getUniqueId());
        if (history != null && history.size() > 1) {
            sender.sendMessage(tl("seenAccounts", StringUtil.joinListSkip(", ", user.getName(), history)));
        }

        if (BanLookup.isBanned(ess, user)) {
            final BanEntry banEntry = BanLookup.getBanEntry(ess, user.getName());
            final String reason = showBan ? banEntry.getReason() : tl("true");
            sender.sendMessage(tl("whoisBanned", reason));
            if (banEntry.getExpiration() != null) {
                Date expiry = banEntry.getExpiration();
                String expireString = tl("now");
                if (expiry.after(new Date())) {
                    expireString = DateUtil.formatDateDiff(expiry.getTime());
                }
                sender.sendMessage(tl("whoisTempBanned", expireString));
            }
        }

        if (user.isMuted()) {
            long muteTimeout = user.getMuteTimeout();
            if (!user.hasMuteReason()) {
                sender.sendMessage(tl("whoisMuted", (muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : tl("true"))));
            } else {
                sender.sendMessage(tl("whoisMutedReason", (muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : tl("true")), user.getMuteReason()));
            }
        }

        final String location = user.getGeoLocation();
        if (location != null && (!(sender.isPlayer()) || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show"))) {
            sender.sendMessage(tl("whoisGeoLocation", location));
        }
        if (showIp) {
            if (!user.getLastLoginAddress().isEmpty()) {
                sender.sendMessage(tl("whoisIPAddress", user.getLastLoginAddress()));
            }
        }
        if (showLocation) {
            final Location loc = user.getLogoutLocation();
            if (loc != null) {
                sender.sendMessage(tl("whoisLocation", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            }
        }
    }

    private void seenIP(final CommandSource sender, final String ipAddress) {
        final UserMap userMap = ess.getUserMap();

        if (ess.getServer().getBanList(BanList.Type.IP).isBanned(ipAddress)) {
            sender.sendMessage(tl("isIpBanned", ipAddress));
        }

        sender.sendMessage(tl("runningPlayerMatch", ipAddress));

        ess.runTaskAsynchronously(() -> {
            final List<String> matches = new ArrayList<>();
            for (final UUID u : userMap.getAllUniqueUsers()) {
                final User user = ess.getUserMap().getUser(u);
                if (user == null) {
                    continue;
                }

                final String uIPAddress = user.getLastLoginAddress();

                if (!uIPAddress.isEmpty() && uIPAddress.equalsIgnoreCase(ipAddress)) {
                    matches.add(user.getName());
                }
            }

            if (matches.size() > 0) {
                sender.sendMessage(tl("matchingIPAddress"));
                sender.sendMessage(StringUtil.joinList(matches));
            } else {
                sender.sendMessage(tl("noMatchingPlayers"));
            }

        });

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
