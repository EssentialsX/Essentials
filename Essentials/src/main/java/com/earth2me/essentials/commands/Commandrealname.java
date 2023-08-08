package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;

import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;

public class Commandrealname extends EssentialsCommand {
    public Commandrealname() {
        super("realname");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        final String lookup = args[0].toLowerCase(Locale.ENGLISH);

        final boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).canInteractVanished();
        boolean foundUser = false;
        for (final User u : ess.getOnlineUsers()) {
            if (skipHidden && u.isHidden(sender.getPlayer()) && u.isHiddenFrom(sender.getPlayer())) {
                continue;
            }
            u.setDisplayNick();
            if (FormatUtil.stripFormat(u.getDisplayName()).toLowerCase(Locale.ENGLISH).contains(lookup)) {
                foundUser = true;
                sender.sendMessage(tl("realName", u.getDisplayName(), u.getName()));
            }
        }
        if (!foundUser) {
            throw new PlayerNotFoundException();
        }
    }
}
