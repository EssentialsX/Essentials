package net.essentialsx.discord.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.EssentialsCommand;
import net.essentialsx.discord.JDADiscordService;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commanddiscord extends EssentialsCommand {
    public Commanddiscord() {
        super("discord");
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) {
        sender.sendMessage(tl("discordCommandLink", ((JDADiscordService) module).getSettings().getDiscordUrl()));
    }
}
