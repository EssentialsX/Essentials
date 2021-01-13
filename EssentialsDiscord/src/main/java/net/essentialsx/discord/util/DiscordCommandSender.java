package net.essentialsx.discord.util;

import com.earth2me.essentials.utils.FormatUtil;
import net.essentialsx.discord.EssentialsJDA;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class DiscordCommandSender implements ConsoleCommandSender {
    private final ConsoleCommandSender sender;
    private BukkitTask task;
    private String responseBuffer = "";
    private long lastTime = System.currentTimeMillis();

    public DiscordCommandSender(EssentialsJDA jda, ConsoleCommandSender sender, CmdCallback callback) {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(jda.getPlugin(), () -> {
            if (!responseBuffer.isEmpty() && System.currentTimeMillis() - lastTime >= 2500) {
                callback.onMessage(responseBuffer);
                responseBuffer = "";
                lastTime = System.currentTimeMillis();
                return;
            }

            if (System.currentTimeMillis() - lastTime >= 20000) {
                task.cancel();
            }
        }, 0, 20);
        this.sender = sender;
    }

    public interface CmdCallback {
        void onMessage(String message);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        responseBuffer = responseBuffer + (responseBuffer.isEmpty() ? "" : "\n") + MessageUtil.sanitizeDiscordMarkdown(FormatUtil.stripFormat(message));
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void sendMessage(@NotNull String[] messages) {
        sender.sendMessage(messages);
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String message) {
        sendMessage(message);
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String[] messages) {
        sender.sendMessage(uuid, messages);
    }

    @NotNull
    @Override
    public Server getServer() {
        return sender.getServer();
    }

    @NotNull
    @Override
    public String getName() {
        return sender.getName();
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return sender.spigot();
    }

    @Override
    public boolean isConversing() {
        return sender.isConversing();
    }

    @Override
    public void acceptConversationInput(@NotNull String input) {
        sender.acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(@NotNull Conversation conversation) {
        return sender.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation) {
        sender.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent details) {
        sender.abandonConversation(conversation, details);
    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        sender.sendRawMessage(message);
    }

    @Override
    public void sendRawMessage(@Nullable UUID uuid, @NotNull String message) {
        sender.sendRawMessage(uuid, message);
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return sender.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return sender.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return sender.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return sender.hasPermission(perm);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return sender.addAttachment(plugin, name, value);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return sender.addAttachment(plugin);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return sender.addAttachment(plugin, name, value, ticks);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return sender.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        sender.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        sender.recalculatePermissions();
    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return sender.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return sender.isOp();
    }

    @Override
    public void setOp(boolean value) {
        sender.setOp(value);
    }
}
