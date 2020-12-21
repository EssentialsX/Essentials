package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.ChatColor;
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
            sender.sendMessage(ChatColor.RED + "There are no players matching that name.");
            return;
        }

        final String message = getFinalArg(args, 1);
        final String senderName = sender.isPlayer() ? ess.getUser(sender.getPlayer()).getDisplayName() : Console.DISPLAY_NAME;
        sender.sendMessage("[" + senderName + ">" + address + "] " + message);
        if (!EssentialsXMPP.getInstance().sendMessage(address, "[" + senderName + "] " + message)) {
            sender.sendMessage(ChatColor.RED + "Error sending message.");
        }
    }
}
