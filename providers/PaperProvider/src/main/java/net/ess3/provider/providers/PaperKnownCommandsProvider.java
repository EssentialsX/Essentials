package net.ess3.provider.providers;

import net.ess3.provider.KnownCommandsProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import java.util.Map;

@ProviderData(description = "Paper Known Commands Provider", weight = 1)
public class PaperKnownCommandsProvider implements KnownCommandsProvider {
    @Override
    public Map<String, Command> getKnownCommands() {
        return Bukkit.getCommandMap().getKnownCommands();
    }

    @ProviderTest
    public static boolean test() {
        try {
            Bukkit.class.getDeclaredMethod("getCommandMap");
            CommandMap.class.getDeclaredMethod("getKnownCommands");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
