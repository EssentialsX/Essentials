package net.ess3.provider.providers;

import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class PaperCommandSender extends BukkitSenderProvider {
    public PaperCommandSender(ConsoleCommandSender base, MessageHook hook) {
        super(base, hook);
    }

    @Override
    public void sendMessage(Identity identity, Component message, MessageType type) {
        sendDumbComponent(message);
    }

    @Override
    public void sendMessage(ComponentLike message) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(Identified source, ComponentLike message) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(Identity source, ComponentLike message) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(Component message) {
        sendDumbComponent(message);
    }

    @Override
    public void sendMessage(Identified source, Component message) {
        sendDumbComponent(message);
    }

    @Override
    public void sendMessage(Identity source, Component message) {
        sendDumbComponent(message);
    }

    @Override
    public void sendMessage(ComponentLike message, MessageType type) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(Identified source, ComponentLike message, MessageType type) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(Identity source, ComponentLike message, MessageType type) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(Component message, MessageType type) {
        sendDumbComponent(message);
    }

    @Override
    public void sendMessage(Identified source, Component message, MessageType type) {
        sendDumbComponent(message);
    }

    public void sendDumbComponent(Component message) {
        sendMessage(Bukkit.getUnsafe().legacyComponentSerializer().serialize(message));
    }
}
