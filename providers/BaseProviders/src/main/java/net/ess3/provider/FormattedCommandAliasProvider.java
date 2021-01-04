package net.ess3.provider;

import org.bukkit.command.CommandSender;
import org.bukkit.command.FormattedCommandAlias;

import java.util.ArrayList;
import java.util.List;

public abstract class FormattedCommandAliasProvider {

    public List<String> createCommands(FormattedCommandAlias command, CommandSender sender, String[] args) {
        final List<String> commands = new ArrayList<>();
        for (String formatString : getFormatStrings(command)) {
            final String cmd;
            try {
                cmd = buildCommand(command, sender, formatString, args);
            } catch (Throwable th) {
                continue; // Ignore, let server handle this.
            }

            if (cmd == null) continue;
            commands.add(cmd.trim());
        }
        return commands;
    }

    public abstract String[] getFormatStrings(FormattedCommandAlias command);

    public abstract String buildCommand(FormattedCommandAlias command, CommandSender sender, String formatString, String[] args);
}
