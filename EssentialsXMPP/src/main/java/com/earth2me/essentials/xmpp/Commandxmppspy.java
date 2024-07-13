package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsLoopCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import net.ess3.api.TranslatableException;
import org.bukkit.Server;

public class Commandxmppspy extends EssentialsLoopCommand {
    public Commandxmppspy() {
        super("xmppspy");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws NotEnoughArgumentsException, TranslatableException {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        loopOnlinePlayers(server, sender, false, true, args[0], args);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User user, final String[] args) {
        try {
            sender.sendMessage("XMPP Spy " + (EssentialsXMPP.getInstance().toggleSpy(user.getBase()) ? "enabled" : "disabled") + " for " + user.getDisplayName());
        } catch (final Exception ex) {
            sender.sendMessage("Error: " + ex.getMessage());
        }
    }
}
