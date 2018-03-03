package com.neximation.essentials.xmpp;

import com.neximation.essentials.CommandSource;
import com.neximation.essentials.Console;
import com.neximation.essentials.commands.EssentialsCommand;
import com.neximation.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Server;


public class Commandxmpp extends EssentialsCommand {
    public Commandxmpp() {
        super("xmpp");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws NotEnoughArgumentsException {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final String address = EssentialsXMPP.getInstance().getAddress(args[0]);
        if (address == null) {
            sender.sendMessage("§cThere are no players matching that name.");
        } else {
            final String message = getFinalArg(args, 1);
            final String senderName = sender.isPlayer() ? ess.getUser(sender.getPlayer()).getDisplayName() : Console.NAME;
            sender.sendMessage("[" + senderName + ">" + address + "] " + message);
            if (!EssentialsXMPP.getInstance().sendMessage(address, "[" + senderName + "] " + message)) {
                sender.sendMessage("§cError sending message.");
            }
        }
    }
}
