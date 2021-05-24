package net.essentialsx.discordlink;

import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandlink extends EssentialsCommand {
    public Commandlink() {
        super("link");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) {
        final AccountLinkManager manager = (AccountLinkManager) module;
        try {
            final String code = manager.createCode(user.getBase().getUniqueId());
            user.sendMessage(tl("discordLinkLinked", "/link " + code));
        } catch (final IllegalArgumentException e) {
            user.sendMessage(e.getMessage());
        }
    }
}
