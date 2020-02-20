package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import net.ess3.api.events.BanStatusChangeEvent;
import org.bukkit.BanList;
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

        User user;
        try {
            user = getPlayer(server, args, 0, true, true);
        } catch (PlayerNotFoundException e) {
            user = ess.getUser(new OfflinePlayer(args[0], ess.getServer()));
            if (!user.getBase().isBanned()) {
                throw new Exception(tl("playerNotFound"), e);
            }
        }

        final User controller = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null;
        BanStatusChangeEvent event = new BanStatusChangeEvent(user, controller, false, null);
        ess.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            ess.getServer().getBanList(BanList.Type.NAME).pardon(user.getName());

            final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
            server.getLogger().log(Level.INFO, tl("playerUnbanned", senderName, user.getName()));

            ess.broadcastMessage("essentials.ban.notify", tl("playerUnbanned", senderName, user.getName()));
        }
    }
}
