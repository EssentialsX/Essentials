package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.PlayerList;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private void sendGroupedList(final CommandSource sender, final String commandLabel, final Map<String, List<User>> playerList) {
        for (final String str : PlayerList.prepareGroupedList(ess, sender, commandLabel, playerList)) {
            sender.sendMessage(str);
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(PlayerList.getPlayerLists(ess, sender.getUser(), false).keySet());
        } else {
            return Collections.emptyList();
        }
    }
}
