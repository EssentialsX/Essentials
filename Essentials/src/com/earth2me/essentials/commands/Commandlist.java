package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.PlayerList;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.Server;

import java.util.*;

import static com.earth2me.essentials.I18n.tl;


public class Commandlist extends EssentialsCommand {
    public Commandlist() {
        super("list");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        boolean showHidden = true;
        User user = null;
        if (sender.isPlayer()) {
            user = ess.getUser(sender.getPlayer());
            showHidden = user.isAuthorized("essentials.list.hidden") || user.canInteractVanished();
        }
        sender.sendMessage(PlayerList.listSummary(ess, user, showHidden));
        final Map<String, List<User>> playerList = PlayerList.getPlayerLists(ess, user, showHidden);

        if (args.length > 0) {
            sender.sendMessage(PlayerList.listGroupUsers(ess, playerList, args[0].toLowerCase()));
        } else {
            sendGroupedList(sender, commandLabel, playerList);
        }
    }

    // Output the standard /list output, when no group is specified
    private void sendGroupedList(CommandSource sender, String commandLabel, Map<String, List<User>> playerList) {
        final Set<String> configGroups = ess.getSettings().getListGroupConfig().keySet();
        final List<String> asterisk = new ArrayList<>();

        // Loop through the custom defined groups and display them
        for (String oConfigGroup : configGroups) {
            String groupValue = ess.getSettings().getListGroupConfig().get(oConfigGroup).toString().trim();
            String configGroup = oConfigGroup.toLowerCase();

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

            List<User> outputUserList;
            final List<User> matchedList = playerList.get(configGroup);

            // If the group value is an int, then we might need to truncate it
            if (NumberUtil.isInt(groupValue)) {
                if (matchedList != null && !matchedList.isEmpty()) {
                    playerList.remove(configGroup);
                    outputUserList = new ArrayList<>(matchedList);
                    int limit = Integer.parseInt(groupValue);
                    if (matchedList.size() > limit) {
                        sender.sendMessage(PlayerList.outputFormat(oConfigGroup, tl("groupNumber", matchedList.size(), commandLabel, FormatUtil.stripFormat(configGroup))));
                    } else {
                        sender.sendMessage(PlayerList.outputFormat(oConfigGroup, PlayerList.listUsers(ess, outputUserList, ", ")));
                    }
                    continue;
                }
            }

            outputUserList = PlayerList.getMergedList(ess, playerList, configGroup);

            // If we have no users, than we don't need to continue parsing this group
            if (outputUserList == null || outputUserList.isEmpty()) {
                continue;
            }

            sender.sendMessage(PlayerList.outputFormat(oConfigGroup, PlayerList.listUsers(ess, outputUserList, ", ")));
        }

        Set<String> var = playerList.keySet();
        String[] onlineGroups = var.toArray(new String[0]);
        Arrays.sort(onlineGroups, String.CASE_INSENSITIVE_ORDER);

        // If we have an asterisk group, then merge all remaining groups
        if (!asterisk.isEmpty()) {
            List<User> asteriskUsers = new ArrayList<>();
            for (String onlineGroup : onlineGroups) {
                asteriskUsers.addAll(playerList.get(onlineGroup));
            }
            for (String key : asterisk) {
                playerList.put(key, asteriskUsers);
            }
            onlineGroups = asterisk.toArray(new String[0]);
        }

        // If we have any groups remaining after the custom groups loop through and display them
        for (String onlineGroup : onlineGroups) {
            List<User> users = playerList.get(onlineGroup);
            String groupName = asterisk.isEmpty() ? users.get(0).getGroup() : onlineGroup;

            if (ess.getPermissionsHandler().getName().equals("ConfigPermissions")) {
                groupName = tl("connectedPlayers");
            }
            if (users == null || users.isEmpty()) {
                continue;
            }

            sender.sendMessage(PlayerList.outputFormat(groupName, PlayerList.listUsers(ess, users, ", ")));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getGroups();
        } else {
            return Collections.emptyList();
        }
    }
}
