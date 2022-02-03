package net.ess3.provider.providers;

import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class PaperCommandSender extends BukkitSenderProvider {
    public PaperCommandSender(ConsoleCommandSender base, MessageHook hook) {
        super(base, hook);
    }

    @Override
    public void sendMessage(@NotNull Identity identity, @NotNull Component message, @NotNull MessageType type) {
        sendDumbComponent(message);
    }

    @Override
    public void sendMessage(ComponentLike message) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(@NotNull Identified source, ComponentLike message) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(@NotNull Identity source, ComponentLike message) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        sendDumbComponent(message);
    }

    @Override
    public void sendMessage(@NotNull Identified source, @NotNull Component message) {
        sendDumbComponent(message);
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message) {
        sendDumbComponent(message);
    }

    @Override
    public void sendMessage(ComponentLike message, @NotNull MessageType type) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(@NotNull Identified source, ComponentLike message, @NotNull MessageType type) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(@NotNull Identity source, ComponentLike message, @NotNull MessageType type) {
        sendDumbComponent(message.asComponent());
    }

    @Override
    public void sendMessage(@NotNull Component message, @NotNull MessageType type) {
        sendDumbComponent(message);
    }

    @Override
    public void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
        sendDumbComponent(message);
    }

    public void sendDumbComponent(Component message) {
        sendMessage(PaperComponents.legacySectionSerializer().serialize(message));
    }

    @Override
    public @NotNull Component name() {
        return base.name();
    }
}
