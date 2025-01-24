package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.KnownCommandsProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@ProviderData(description = "Reflection Known Commands Provider")
public class ReflKnownCommandsProvider implements KnownCommandsProvider {
    private final Map<String, Command> knownCommands;

    public ReflKnownCommandsProvider() {
        Map<String, Command> knownCommands = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends Server> craftServerClass = (Class<? extends Server>) ReflUtil.getOBCClass("CraftServer");
            if (craftServerClass != null) {
                final Field commandMapField = ReflUtil.getFieldCached(craftServerClass, "commandMap");
                if (commandMapField != null) {
                    final SimpleCommandMap simpleCommandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());
                    final Field knownCommandsField = ReflUtil.getFieldCached(SimpleCommandMap.class, "knownCommands");
                    if (knownCommandsField != null) {
                        //noinspection unchecked
                        knownCommands = (Map<String, Command>) knownCommandsField.get(simpleCommandMap);
                    }
                }
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        } finally {
            this.knownCommands = knownCommands;
        }
    }

    @Override
    public Map<String, Command> getKnownCommands() {
        return this.knownCommands;
    }
}
