package net.ess3.provider;

import org.bukkit.command.Command;

import java.util.Map;

public interface KnownCommandsProvider extends Provider {
    Map<String, Command> getKnownCommands();
}
