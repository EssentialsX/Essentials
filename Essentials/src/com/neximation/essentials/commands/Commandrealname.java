package com.neximation.essentials.commands;

import com.neximation.essentials.CommandSource;
import com.neximation.essentials.User;
import com.neximation.essentials.utils.FormatUtil;
import org.bukkit.Server;

import java.util.Locale;

import static com.neximation.essentials.I18n.tl;


public class Commandrealname extends EssentialsCommand {
    public Commandrealname() {
        super("realname");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        final String whois = args[0].toLowerCase(Locale.ENGLISH);
        boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).canInteractVanished();
        boolean foundUser = false;
        for (User u : ess.getOnlineUsers()) {
            if (skipHidden && u.isHidden(sender.getPlayer()) && !sender.getPlayer().canSee(u.getBase())) {
                continue;
            }
            u.setDisplayNick();
            final String displayName = FormatUtil.stripFormat(u.getDisplayName()).toLowerCase(Locale.ENGLISH);
            if (displayName.contains(whois)) {
                foundUser = true;
                sender.sendMessage(u.getDisplayName() + " " + tl("is") + " " + u.getName());
            }
        }
        if (!foundUser) {
            throw new PlayerNotFoundException();
        }
    }
}
