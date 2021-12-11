package net.ess3.provider.providers;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public final class ModernCommandSenderSpigotCreator {
    private ModernCommandSenderSpigotCreator() {
    }

    /**
     * The JVM will FOR SOME REASON try to load inner classes even BEFORE THE CODE WHICH REFERENCE THEM IS CALLED.
     * This dumbass hack postpones the class lookup to until we know for sure the class exists.
     */
    public static CommandSender.Spigot stupidDumbHackToMakeTheJvmHappy(BukkitSenderProvider provider) {
        return new CommandSender.Spigot() {
            @Override
            public void sendMessage(BaseComponent component) {
                provider.sendMessage(component.toLegacyText());
            }

            @Override
            public void sendMessage(BaseComponent... components) {
                sendMessage(new TextComponent(components));
            }

            @Override
            public void sendMessage(UUID sender, BaseComponent... components) {
                sendMessage(components);
            }

            @Override
            public void sendMessage(UUID sender, BaseComponent component) {
                sendMessage(component);
            }
        };
    }
}
