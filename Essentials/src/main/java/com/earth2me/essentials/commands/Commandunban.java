package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import org.bukkit.BanList;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class Commandunban extends EssentialsCommand {
    public Commandunban() {
        super("unban");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String name;
        try {
            final User user = getPlayer(server, args, 0, true, true);
            name = user.getName();
            ess.getServer().getBanList(BanList.Type.NAME).pardon(name);
        } catch (final PlayerNotFoundException e) {
            final OfflinePlayer player = server.getOfflinePlayer(args[0]);
            name = player.getName();
            if (!player.isBanned()) {
                throw new Exception(tl("playerNeverOnServer", args[0]));
            }
            ess.getServer().getBanList(BanList.Type.NAME).pardon(name);
        }

        final String senderDisplayName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.DISPLAY_NAME;
        ess.getLogger().log(Level.INFO, tl("playerUnbanned", senderDisplayName, name));

        ess.broadcastMessage("essentials.ban.notify", tl("playerUnbanned", senderDisplayName, name));
    }
}
