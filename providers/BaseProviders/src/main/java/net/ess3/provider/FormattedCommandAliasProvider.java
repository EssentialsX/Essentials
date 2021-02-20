package net.ess3.provider;

import org.bukkit.command.CommandSender;
import org.bukkit.command.FormattedCommandAlias;

import java.util.List;

public interface FormattedCommandAliasProvider extends Provider {

    List<String> createCommands(FormattedCommandAlias command, CommandSender sender, String[] args);

    String[] getFormatStrings(FormattedCommandAlias command);

    String buildCommand(FormattedCommandAlias command, CommandSender sender, String formatString, String[] args);
}
