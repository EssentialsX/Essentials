package com.neximation.essentials.commands;

import com.neximation.essentials.CommandSource;
import com.neximation.essentials.utils.StringUtil;
import org.bukkit.Server;

import java.util.Collection;

import static com.neximation.essentials.I18n.tl;


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
