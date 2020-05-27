package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;


public class Commandjails extends EssentialsCommand {
    public Commandjails() {
        super("jails");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (ess.getJails().getCount() < 1) {
            sender.sendMessage(tl("noJailsDefined"));
        } else {
            sender.sendMessage(tl("jailList", StringUtil.joinList(" ", ess.getJails().getList())));
        }
    }
}
