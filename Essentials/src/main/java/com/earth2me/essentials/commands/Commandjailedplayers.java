package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class Commandjailedplayers extends EssentialsCommand {
    public Commandjailedplayers() {
        super("jailedplayers");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (ess.getJailedPlayers() != null && !ess.getJailedPlayers().isEmpty()) {
            sender.sendTl("jailedPlayersList", StringUtil.joinList(", ", ess.getJailedPlayers().stream().map(Player::getName).collect(Collectors.toList())));
        } else {
            sender.sendTl("jailedPlayersEmpty");
        }
    }
}
