package net.essentialsx.discordlink.commands.bukkit;

import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import net.essentialsx.discordlink.AccountLinkManager;
import org.bukkit.Server;

public class Commandlink extends EssentialsCommand {
    public Commandlink() {
        super("link");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) {
        final AccountLinkManager manager = (AccountLinkManager) module;
        if (manager.isLinked(user.getUUID())) {
            user.sendTl("discordLinkLinkedAlready");
            return;
        }

        try {
            final String code = manager.createCode(user.getBase().getUniqueId());
            user.sendTl("discordLinkLinked", "/link " + code);
        } catch (final IllegalArgumentException e) {
            user.sendTl("discordLinkPending", "/link " + e.getMessage());
        }
    }
}
