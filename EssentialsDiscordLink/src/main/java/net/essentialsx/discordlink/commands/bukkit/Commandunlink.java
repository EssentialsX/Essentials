package net.essentialsx.discordlink.commands.bukkit;

import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import net.essentialsx.api.v2.events.discordlink.DiscordLinkStatusChangeEvent;
import net.essentialsx.discordlink.AccountLinkManager;
import org.bukkit.Server;

public class Commandunlink extends EssentialsCommand {
    public Commandunlink() {
        super("unlink");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) {
        final AccountLinkManager manager = (AccountLinkManager) module;
        if (!manager.removeAccount(user, DiscordLinkStatusChangeEvent.Cause.UNSYNC_PLAYER)) {
            user.sendTl("discordLinkNoAccount");
            return;
        }

        user.sendTl("discordLinkUnlinked");
    }
}
