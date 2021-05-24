package net.essentialsx.discordlink;

import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.Server;

public class Commandlink extends EssentialsCommand {
    public Commandlink() {
        super("link");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) {
        final AccountLinkManager manager = (AccountLinkManager) module;
        try {
            final String code = manager.createCode(user.getBase().getUniqueId());
            user.sendMessage("To link this minecraft account to a discord account, type /link " + code + " in discord in order to link this account.");
        } catch (final IllegalArgumentException e) {
            user.sendMessage(e.getMessage());
        }
    }
}
