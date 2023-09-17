package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.BanLookup;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.CommonPlaceholders;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.StringUtil;
import net.ess3.api.TranslatableException;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Commandseen extends EssentialsCommand {
    public Commandseen() {
        super("seen");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        final boolean showBan = sender.isAuthorized("essentials.seen.banreason");
        final boolean showIp = sender.isAuthorized("essentials.seen.ip");
        final boolean showLocation = sender.isAuthorized("essentials.seen.location");
        final boolean searchAccounts = commandLabel.contains("alts") && sender.isAuthorized("essentials.seen.alts");

        User player;
        // Check by uuid, if it fails check by name.
        try {
            final UUID uuid = UUID.fromString(args[0]);
            player = ess.getUser(uuid);
        } catch (final IllegalArgumentException ignored) { // Thrown if invalid UUID from string, check by name.
            player = ess.getOfflineUser(args[0]);
        }

        if (player == null) {
            if (!searchAccounts) {
                if (sender.isAuthorized("essentials.seen.ipsearch") && FormatUtil.validIP(args[0])) {
                    if (ess.getServer().getBanList(BanList.Type.IP).isBanned(args[0])) {
                        sender.sendTl("isIpBanned", args[0]);
                    }
                    seenIP(sender, args[0], args[0]);
                    return;
                } else if (ess.getServer().getBanList(BanList.Type.IP).isBanned(args[0])) {
                    sender.sendTl("isIpBanned", args[0]);
                    return;
                } else if (BanLookup.isBanned(ess, args[0])) {
                    sender.sendTl("whoisBanned", showBan ? BanLookup.getBanEntry(ess, args[0]).getReason() : sender.tl("true"));
                    return;
                }
            }
            ess.getScheduler().runTaskAsynchronously(ess, new Runnable() {
                @Override
                public void run() {
                    final User userFromBukkit = ess.getUsers().getUser(args[0]);
                    try {
                        if (userFromBukkit != null) {
                            showUserSeen(userFromBukkit);
                        } else {
                            try {
                                showUserSeen(getPlayer(server, sender, args, 0));
                            } catch (final PlayerNotFoundException e) {
                                throw new TranslatableException("playerNeverOnServer", args[0]);
                            }
                        }
                    } catch (final Exception e) {
                        ess.showError(sender, e, commandLabel);
                    }
                }

                private void showUserSeen(final User user) {
                    showSeenMessage(sender, user, searchAccounts, showBan, showIp, showLocation);
                }
            });
        } else {
            showSeenMessage(sender, player, searchAccounts, showBan, showIp, showLocation);
        }
    }

    private void showSeenMessage(final CommandSource sender, final User player, final boolean searchAccounts, final boolean showBan, final boolean showIp, final boolean showLocation) {
        if (searchAccounts) {
            seenIP(sender, player.getLastLoginAddress(), player.getDisplayName());
        } else if (player.getBase().isOnline() && canInteractWith(sender, player)) {
            seenOnline(sender, player, showIp);
        } else {
            seenOffline(sender, player, showBan, showIp, showLocation);
        }
    }

    private void seenOnline(final CommandSource sender, final User user, final boolean showIp) {

        user.setDisplayNick();
        sender.sendTl("seenOnline", user.getDisplayName(), DateUtil.formatDateDiff(user.getLastLogin()));

        final List<String> history = user.getPastUsernames();
        if (history != null && !history.isEmpty()) {
            sender.sendTl("seenAccounts", StringUtil.joinListSkip(", ", user.getName(), history));
        }

        if (sender.isAuthorized("essentials.seen.uuid")) {
            sender.sendTl("whoisUuid", user.getBase().getUniqueId().toString());
        }

        if (user.isAfk()) {
            sender.sendTl("whoisAFK", CommonPlaceholders.trueFalse(sender, true));
        }
        if (user.isJailed()) {
            sender.sendTl("whoisJail", user.getJailTimeout() > 0 ? user.getFormattedJailTime() : CommonPlaceholders.trueFalse(sender, true));
        }
        if (user.isMuted()) {
            final long muteTimeout = user.getMuteTimeout();
            if (!user.hasMuteReason()) {
                sender.sendTl("whoisMuted", muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : CommonPlaceholders.trueFalse(sender, true));
            } else {
                sender.sendTl("whoisMutedReason", muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : CommonPlaceholders.trueFalse(sender, true), user.getMuteReason());
            }
        }
        final String location = user.getGeoLocation();
        if (location != null && (!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show"))) {
            sender.sendTl("whoisGeoLocation", location);
        }
        if (showIp) {
            sender.sendTl("whoisIPAddress", user.getBase().getAddress().getAddress().toString());
        }
    }

    private void seenOffline(final CommandSource sender, final User user, final boolean showBan, final boolean showIp, final boolean showLocation) {
        user.setDisplayNick();
        if (user.getLastLogout() > 0) {
            sender.sendTl("seenOffline", user.getName(), DateUtil.formatDateDiff(user.getLastLogout()));
            final List<String> history = user.getPastUsernames();
            if (history != null && history.size() > 1) {
                sender.sendTl("seenAccounts", StringUtil.joinListSkip(", ", user.getName(), history));
            }

            if (sender.isAuthorized("essentials.seen.uuid")) {
                sender.sendTl("whoisUuid", user.getBase().getUniqueId());
            }
        } else {
            sender.sendTl("userUnknown", user.getName());
        }

        if (BanLookup.isBanned(ess, user)) {
            final BanEntry banEntry = BanLookup.getBanEntry(ess, user.getName());
            final Object reason = showBan ? banEntry.getReason() : CommonPlaceholders.trueFalse(sender, true);
            sender.sendTl("whoisBanned", reason);
            if (banEntry.getExpiration() != null) {
                final Date expiry = banEntry.getExpiration();
                Object expireString = AdventureUtil.parsed(sender.tl("now"));
                if (expiry.after(new Date())) {
                    expireString = DateUtil.formatDateDiff(expiry.getTime());
                }
                sender.sendTl("whoisTempBanned", expireString);
            }
        }

        if (user.isMuted()) {
            final long muteTimeout = user.getMuteTimeout();
            if (!user.hasMuteReason()) {
                sender.sendTl("whoisMuted", muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : CommonPlaceholders.trueFalse(sender, true));
            } else {
                sender.sendTl("whoisMutedReason", muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : CommonPlaceholders.trueFalse(sender, true), user.getMuteReason());
            }
        }

        final String location = user.getGeoLocation();
        if (location != null && (!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show"))) {
            sender.sendTl("whoisGeoLocation", location);
        }
        if (showIp) {
            if (!user.getLastLoginAddress().isEmpty()) {
                sender.sendTl("whoisIPAddress", user.getLastLoginAddress());
            }
        }
        if (showLocation) {
            final Location loc = user.getLogoutLocation();
            if (loc != null) {
                sender.sendTl("whoisLocation", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            }
        }
    }

    private void seenIP(final CommandSource sender, final String ipAddress, final String display) {
        sender.sendTl("runningPlayerMatch", display);

        ess.runTaskAsynchronously(() -> {
            final List<String> matches = new ArrayList<>();
            for (final UUID u : ess.getUsers().getAllUserUUIDs()) {
                final User user = ess.getUsers().loadUncachedUser(u);
                if (user == null) {
                    continue;
                }

                final String uIPAddress = user.getLastLoginAddress();

                if (!uIPAddress.isEmpty() && uIPAddress.equalsIgnoreCase(ipAddress)) {
                    matches.add(user.getName());
                }
            }

            if (matches.size() > 0) {
                sender.sendTl("matchingIPAddress");
                sender.sendMessage(StringUtil.joinList(matches));
            } else {
                sender.sendTl("noMatchingPlayers");
            }

        });

    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
