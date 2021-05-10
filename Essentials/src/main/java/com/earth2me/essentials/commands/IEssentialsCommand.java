package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public interface IEssentialsCommand {
    String getName();

    Map<String, String> getUsageStrings();

    void run(Server server, User user, String commandLabel, Command cmd, String[] args) throws Exception;

    void run(Server server, CommandSource sender, String commandLabel, Command cmd, String[] args) throws Exception;

    List<String> tabComplete(Server server, User user, String commandLabel, Command cmd, String[] args);

    List<String> tabComplete(Server server, CommandSource sender, String commandLabel, Command cmd, String[] args);

    void setEssentials(IEssentials ess);

    void setEssentialsModule(IEssentialsModule module);

    void showError(CommandSender sender, Throwable throwable, String commandLabel);
}
