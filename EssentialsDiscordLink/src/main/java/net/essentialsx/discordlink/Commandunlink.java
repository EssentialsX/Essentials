package net.essentialsx.discordlink;

import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.Server;

public class Commandunlink extends EssentialsCommand {
    public Commandunlink() {
        super("unlink");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        final AccountLinkManager manager = (AccountLinkManager) module;
        if (!manager.removeAccount(user.getBase().getUniqueId())) {
            user.sendMessage("You do not currently have a discord account linked to this minecraft account!");
            return;
        }

        user.sendMessage("Unlinked this minecraft account from all associated discord accounts.");
    }
}
