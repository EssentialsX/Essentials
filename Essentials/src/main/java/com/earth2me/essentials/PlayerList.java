package com.earth2me.essentials;

import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.TranslatableException;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.earth2me.essentials.I18n.tlLiteral;

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
                groupString.append(tlLiteral("listAfkTag"));
            }
            if (user.isHidden()) {
                groupString.append(tlLiteral("listHiddenTag"));
            }
            user.setDisplayNick();
            groupString.append(AdventureUtil.legacyToMini(user.getDisplayName()));

            final String strippedNick = FormatUtil.stripFormat(user.getNickname());
            if (ess.getSettings().realNamesOnList() && strippedNick != null && !strippedNick.equals(user.getName())) {
                groupString.append(" ").append(tlLiteral("listRealName",user.getName()));
            }
            groupString.append("<white>");
        }
        return groupString.toString();
    }

    // Produce a user summary: There are 5 out of maximum 10 players online.
    public static String listSummary(final IEssentials ess, final User user, final boolean showHidden) {
        final Server server = ess.getServer();
        int playerHidden = 0;
        int hiddenCount = 0;
        for (final User onlinePlayer : ess.getOnlineUsers()) {
            if (onlinePlayer.isHidden() || (user != null && onlinePlayer.isHiddenFrom(user.getBase()))) {
                playerHidden++;
                if (showHidden || user != null && !onlinePlayer.isHiddenFrom(user.getBase())) {
                    hiddenCount++;
                }
            }
        }

        final String tlKey;
        final Object[] objects;
        if (hiddenCount > 0) {
            tlKey = "listAmountHidden";
            objects = new Object[]{ess.getOnlinePlayers().size() - playerHidden, hiddenCount, server.getMaxPlayers()};
        } else {
            tlKey = "listAmount";
            objects = new Object[]{ess.getOnlinePlayers().size() - playerHidden, server.getMaxPlayers()};
        }
        return user == null ? tlLiteral(tlKey, objects) : user.playerTl(tlKey, objects);
    }

    // Build the basic player list, divided by groups.
    public static Map<String, List<User>> getPlayerLists(final IEssentials ess, final IUser sender, final boolean showHidden) {
        final Map<String, List<User>> playerList = new HashMap<>();
        for (final User onlineUser : ess.getOnlineUsers()) {
            if ((sender == null && !showHidden && onlineUser.isHidden()) || (sender != null && !showHidden && onlineUser.isHiddenFrom(sender.getBase()))) {
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
            throw new TranslatableException("groupDoesNotExist");
        }
        final String displayGroupName = Character.toTitleCase(groupName.charAt(0)) +
            groupName.substring(1);
        return outputFormat(displayGroupName, listUsers(ess, users, ", "));
    }

    // Build the output string
    public static String outputFormat(final String group, final String message) {
        return tlLiteral("listGroupTag", FormatUtil.replaceFormat(group)) +
            message;
    }

    public static List<String> prepareGroupedList(final IEssentials ess, final CommandSource source, final String commandLabel, final Map<String, List<User>> playerList) {
        final List<String> output = new ArrayList<>();

        final Set<String> configGroups = ess.getSettings().getListGroupConfig().keySet();
        final List<String> asterisk = new ArrayList<>();

        // Loop through the custom defined groups and display them
        for (final String oConfigGroup : configGroups) {
            final String groupValue = ess.getSettings().getListGroupConfig().get(oConfigGroup).toString().trim();
            final String configGroup = oConfigGroup.toLowerCase();

            // If the group value is an asterisk, then skip it, and handle it later
            if (groupValue.equals("*")) {
                asterisk.add(oConfigGroup);
                continue;
            }

            // If the group value is hidden, we don't need to display it
            if (groupValue.equalsIgnoreCase("hidden")) {
                playerList.remove(configGroup);
                continue;
            }

            final List<User> outputUserList;
            final List<User> matchedList = playerList.get(configGroup);

            // If the group value is an int, then we might need to truncate it
            if (NumberUtil.isInt(groupValue)) {
                if (matchedList != null && !matchedList.isEmpty()) {
                    playerList.remove(configGroup);
                    outputUserList = new ArrayList<>(matchedList);
                    final int limit = Integer.parseInt(groupValue);
                    if (matchedList.size() > limit) {
                        final String tlKey = "groupNumber";
                        final Object[] objects = {matchedList.size(), commandLabel, FormatUtil.stripFormat(configGroup)};
                        output.add(outputFormat(oConfigGroup, source == null ? tlLiteral(tlKey, objects) : source.tl(tlKey, objects)));
                    } else {
                        output.add(outputFormat(oConfigGroup, listUsers(ess, outputUserList, ", ")));
                    }
                    continue;
                }
            }

            outputUserList = getMergedList(ess, playerList, configGroup);

            // If we have no users, than we don't need to continue parsing this group
            if (outputUserList.isEmpty()) {
                continue;
            }

            output.add(outputFormat(oConfigGroup, listUsers(ess, outputUserList, ", ")));
        }

        final Set<String> var = playerList.keySet();
        String[] onlineGroups = var.toArray(new String[0]);
        Arrays.sort(onlineGroups, String.CASE_INSENSITIVE_ORDER);

        // If we have an asterisk group, then merge all remaining groups
        if (!asterisk.isEmpty()) {
            final List<User> asteriskUsers = new ArrayList<>();
            for (final String onlineGroup : onlineGroups) {
                asteriskUsers.addAll(playerList.get(onlineGroup));
            }
            for (final String key : asterisk) {
                playerList.put(key, asteriskUsers);
            }
            onlineGroups = asterisk.toArray(new String[0]);
        }

        // If we have any groups remaining after the custom groups loop through and display them
        for (final String onlineGroup : onlineGroups) {
            final List<User> users = playerList.get(onlineGroup);
            String groupName = asterisk.isEmpty() ? users.get(0).getGroup() : onlineGroup;

            if (ess.getPermissionsHandler().getName().equals("ConfigPermissions")) {
                groupName = source == null ? tlLiteral("connectedPlayers") : source.tl("connectedPlayers");
            }
            if (users == null || users.isEmpty()) {
                continue;
            }

            output.add(outputFormat(groupName, listUsers(ess, users, ", ")));
        }
        return output;
    }
}
