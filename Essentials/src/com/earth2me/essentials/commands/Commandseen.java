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

import static com.earth2me.essentials.I18n.tlp;


public class Commandseen extends EssentialsCommand {
    public Commandseen() {
        super("seen");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        seen(server, sender, commandLabel, args, true, true, true, true);
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        seen(server, user.getSource(), commandLabel, args, user.isAuthorized("essentials.seen.banreason"), user.isAuthorized("essentials.seen.ip"), user.isAuthorized("essentials.seen.location"), user.isAuthorized("essentials.seen.ipsearch"));
    }

    protected void seen(final Server server, final CommandSource sender, final String commandLabel, final String[] args,
                        final boolean showBan, final boolean showIp, final boolean showLocation, final boolean ipLookup) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        User player;
        // Check by uuid, if it fails check by name.
        try {
            UUID uuid = UUID.fromString(args[0]);
            player = ess.getUser(uuid);
        }catch (IllegalArgumentException ignored) { // Thrown if invalid UUID from string, check by name.
            player = ess.getOfflineUser(args[0]);
        }

        if (player == null) {
            if (ipLookup && FormatUtil.validIP(args[0])) {
                seenIP(server, sender, args[0]);
                return;
            } else if (ess.getServer().getBanList(BanList.Type.IP).isBanned(args[0])) {
                sender.sendMessage(tlp(sender, "isIpBanned", args[0]));
                return;
            } else if (BanLookup.isBanned(ess, args[0])) {
                sender.sendMessage(tlp(sender, "whoisBanned", showBan ? BanLookup.getBanEntry(ess, args[0]).getReason() : tlp(sender, "true")));
                return;
            }
            ess.getScheduler().runTaskAsynchronously(ess, new Runnable() {
                @Override
                public void run() {
                    User userFromBukkit = ess.getUserMap().getUserFromBukkit(args[0]);
                    try {
                        if (userFromBukkit != null) {
                            showUserSeen(userFromBukkit);
                        } else {
                            showUserSeen(getPlayer(server, sender, args, 0));
                        }
                    } catch (Exception e) {
                        ess.showError(sender, e, commandLabel);
                    }
                }

                private void showUserSeen(User user) throws Exception {
                    if (user == null) {
                        throw new PlayerNotFoundException();
                    }
                    showSeenMessage(server, sender, user, showBan, showIp, showLocation);
                }
            });
        } else {
            showSeenMessage(server, sender, player, showBan, showIp, showLocation);
        }
    }

    private void showSeenMessage(Server server, CommandSource sender, User player, boolean showBan, boolean showIp, boolean showLocation)  throws Exception {
        if (player.getBase().isOnline() && canInteractWith(sender, player)) {
            seenOnline(server, sender, player, showBan, showIp, showLocation);
        } else {
            seenOffline(server, sender, player, showBan, showIp, showLocation);
        }
    }

    private void seenOnline(final Server server, final CommandSource sender, final User user, final boolean showBan, final boolean showIp, final boolean showLocation) throws Exception {

        user.setDisplayNick();
        sender.sendMessage(tlp(sender, "seenOnline", user.getDisplayName(), DateUtil.formatDateDiff(user.getLastLogin())));

        if (ess.getSettings().isDebug()) {
            ess.getLogger().info("UUID: " + user.getBase().getUniqueId().toString());
        }

        List<String> history = ess.getUserMap().getUserHistory(user.getBase().getUniqueId());
        if (history != null && history.size() > 1) {
            sender.sendMessage(tlp(sender, "seenAccounts", StringUtil.joinListSkip(", ", user.getName(), history)));
        }

        if (user.isAfk()) {
            sender.sendMessage(tlp(sender, "whoisAFK", tlp(sender, "true")));
        }
        if (user.isJailed()) {
            sender.sendMessage(tlp(sender, "whoisJail", (user.getJailTimeout() > 0 ? DateUtil.formatDateDiff(user.getJailTimeout()) : tlp(sender, "true"))));
        }
        if (user.isMuted()) {
            sender.sendMessage(tlp(sender, "whoisMuted", (user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : tlp(sender, "true"))));
        }
        final String location = user.getGeoLocation();
        if (location != null && (!(sender.isPlayer()) || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show"))) {
            sender.sendMessage(tlp(sender, "whoisGeoLocation", location));
        }
        if (showIp) {
            sender.sendMessage(tlp(sender, "whoisIPAddress", user.getBase().getAddress().getAddress().toString()));
        }
    }

    private void seenOffline(final Server server, final CommandSource sender, User user, final boolean showBan, final boolean showIp, final boolean showLocation) throws Exception {
        user.setDisplayNick();
        if (user.getLastLogout() > 0) {
            sender.sendMessage(tlp(sender, "seenOffline", user.getName(), DateUtil.formatDateDiff(user.getLastLogout())));
        } else {
            sender.sendMessage(tlp(sender, "userUnknown", user.getName()));
        }

        if (ess.getSettings().isDebug()) {
            ess.getLogger().info("UUID: " + user.getBase().getUniqueId().toString());
        }

        List<String> history = ess.getUserMap().getUserHistory(user.getBase().getUniqueId());
        if (history != null && history.size() > 1) {
            sender.sendMessage(tlp(sender, "seenAccounts", StringUtil.joinListSkip(", ", user.getName(), history)));
        }

        if (BanLookup.isBanned(ess, user)) {
            final BanEntry banEntry = BanLookup.getBanEntry(ess, user.getName());
            final String reason = showBan ? banEntry.getReason() : tlp(sender, "true");
            sender.sendMessage(tlp(sender, "whoisBanned", reason));
            if (banEntry.getExpiration() != null) {
                Date expiry = banEntry.getExpiration();
                String expireString = tlp(sender, "now");
                if (expiry.after(new Date())) {
                    expireString = DateUtil.formatDateDiff(expiry.getTime());
                }
                sender.sendMessage(tlp(sender, "whoisTempBanned", expireString));
            }
        }

        final String location = user.getGeoLocation();
        if (location != null && (!(sender.isPlayer()) || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show"))) {
            sender.sendMessage(tlp(sender, "whoisGeoLocation", location));
        }
        if (showIp) {
            if (!user.getLastLoginAddress().isEmpty()) {
                sender.sendMessage(tlp(sender, "whoisIPAddress", user.getLastLoginAddress()));
            }
        }
        if (showLocation) {
            final Location loc = user.getLogoutLocation();
            if (loc != null) {
                sender.sendMessage(tlp(sender, "whoisLocation", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            }
        }
    }

    private void seenIP(final Server server, final CommandSource sender, final String ipAddress) throws Exception {
        final UserMap userMap = ess.getUserMap();

        if (ess.getServer().getBanList(BanList.Type.IP).isBanned(ipAddress)) {
            sender.sendMessage(tlp(sender, "isIpBanned", ipAddress));
        }

        sender.sendMessage(tlp(sender, "runningPlayerMatch", ipAddress));

        ess.runTaskAsynchronously(new Runnable() {
            @Override
            public void run() {
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
                    sender.sendMessage(tlp(sender, "matchingIPAddress"));
                    sender.sendMessage(StringUtil.joinList(matches));
                } else {
                    sender.sendMessage(tlp(sender, "noMatchingPlayers"));
                }

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
