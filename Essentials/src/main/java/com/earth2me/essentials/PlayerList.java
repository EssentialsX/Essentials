package com.earth2me.essentials;

import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.ChatColor;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.earth2me.essentials.I18n.tl;

public final class PlayerList {

    private PlayerList() {
    }

    // Cosmetic list formatting
    public static String listUsers(final IEssentials ess, final List<User> users, final String seperator) {
        final StringBuilder groupString = new StringBuilder();
        Collections.sort(users);
        boolean needComma = false;
        for (final User user : users) {
            if (needComma) {
                groupString.append(seperator);
            }
            needComma = true;
            if (user.isAfk()) {
                groupString.append(tl("listAfkTag"));
            }
            if (user.isHidden()) {
                groupString.append(tl("listHiddenTag"));
            }
            user.setDisplayNick();
            groupString.append(user.getDisplayName());

            final String strippedNick = FormatUtil.stripFormat(user.getNickname());
            if (ess.getSettings().realNamesOnList() && strippedNick != null && !strippedNick.equals(user.getName())) {
                groupString.append(" (").append(user.getName()).append(")");
            }
            groupString.append(ChatColor.WHITE.toString());
        }
        return groupString.toString();
    }

    // Produce a user summary: There are 5 out of maximum 10 players online.
    public static String listSummary(final IEssentials ess, final User user, final boolean showHidden) {
        final Server server = ess.getServer();
        int playerHidden = 0;
        int hiddenCount = 0;
        for (final User onlinePlayer : ess.getOnlineUsers()) {
            if (onlinePlayer.isHidden() || (user != null && !user.getBase().canSee(onlinePlayer.getBase()))) {
                playerHidden++;
                if (showHidden || user != null && user.getBase().canSee(onlinePlayer.getBase())) {
                    hiddenCount++;
                }
            }
        }
        final String online;
        if (hiddenCount > 0) {
            online = tl("listAmountHidden", ess.getOnlinePlayers().size() - playerHidden, hiddenCount, server.getMaxPlayers());
        } else {
            online = tl("listAmount", ess.getOnlinePlayers().size() - playerHidden, server.getMaxPlayers());
        }
        return online;
    }

    // Build the basic player list, divided by groups.
    public static Map<String, List<User>> getPlayerLists(final IEssentials ess, final IUser sender, final boolean showHidden) {
        final Map<String, List<User>> playerList = new HashMap<>();
        for (final User onlineUser : ess.getOnlineUsers()) {
            if ((sender == null && !showHidden && onlineUser.isHidden()) || (sender != null && !showHidden && !sender.getBase().canSee(onlineUser.getBase()))) {
                continue;
            }
            final String group = FormatUtil.stripFormat(FormatUtil.stripEssentialsFormat(onlineUser.getGroup().toLowerCase()));
            final List<User> list = playerList.computeIfAbsent(group, k -> new ArrayList<>());
            list.add(onlineUser);
        }
        return playerList;
    }

    // Handle the merging of groups
    public static List<User> getMergedList(final IEssentials ess, final Map<String, List<User>> playerList, final String groupName) {
        final Set<String> configGroups = ess.getSettings().getListGroupConfig().keySet();
        final List<User> users = new ArrayList<>();
        for (final String configGroup : configGroups) {
            if (configGroup.equalsIgnoreCase(groupName)) {
                final String[] groupValues = ess.getSettings().getListGroupConfig().get(configGroup).toString().trim().split(" ");
                for (String groupValue : groupValues) {
                    groupValue = groupValue.toLowerCase(Locale.ENGLISH);
                    if (groupValue.isEmpty()) {
                        continue;
                    }
                    final List<User> u = playerList.get(groupValue.trim());
                    if (u == null || u.isEmpty()) {
                        continue;
                    }
                    playerList.remove(groupValue);
                    users.addAll(u);
                }
            }
        }
        return users;
    }

    // Output a playerlist of just a single group, /list <groupname>
    public static String listGroupUsers(final IEssentials ess, final Map<String, List<User>> playerList, final String groupName) throws Exception {
        final List<User> users = getMergedList(ess, playerList, groupName);
        final List<User> groupUsers = playerList.get(groupName);
        if (groupUsers != null && !groupUsers.isEmpty()) {
            users.addAll(groupUsers);
        }
        if (users.isEmpty()) {
            throw new Exception(tl("groupDoesNotExist"));
        }
        final String displayGroupName = Character.toTitleCase(groupName.charAt(0)) +
            groupName.substring(1);
        return outputFormat(displayGroupName, listUsers(ess, users, ", "));
    }

    // Build the output string
    public static String outputFormat(final String group, final String message) {
        return tl("listGroupTag", FormatUtil.replaceFormat(group)) +
            message;
    }
}
