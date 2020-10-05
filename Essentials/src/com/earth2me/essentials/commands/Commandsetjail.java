package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandsetjail extends EssentialsCommand {
    public Commandsetjail() {
        super("setjail");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        ess.getJails().setJail(args[0], user.getLocation());
        user.sendMessage(tl("jailSet", StringUtil.sanitizeString(args[0])));
    }
}
