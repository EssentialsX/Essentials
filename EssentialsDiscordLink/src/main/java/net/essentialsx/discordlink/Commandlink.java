package net.essentialsx.discordlink;

import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.Server;

public class Commandlink extends EssentialsCommand {
    protected Commandlink() {
        super("link");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        final EssentialsDiscordLink discord = (EssentialsDiscordLink) module;
    }
}
