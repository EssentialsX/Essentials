package net.ess3.provider.providers;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;

public class BukkitSenderProvider implements CommandSender {
    private final ConsoleCommandSender base;
    private final MessageHook hook;

    public BukkitSenderProvider(ConsoleCommandSender base, MessageHook hook) {
        this.base = base;
        this.hook = hook;
    }

    public interface MessageHook {
        void sendMessage(String message);
    }

    @Override
    public void sendMessage(String message) {
        hook.sendMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String msg : messages) {
            sendMessage(msg);
        }
    }

    @Override
    public void sendMessage(UUID uuid, String message) {
        sendMessage(message);
    }

    @Override
    public void sendMessage(UUID uuid, String[] messages) {
        sendMessage(messages);
    }

    @Override
    public Server getServer() {
        return base.getServer();
    }

    @Override
    public String getName() {
        return base.getName();
    }

    @Override
    public Spigot spigot() {
        try {
            Class.forName("org.bukkit.command.CommandSender$Spigot");
            return ModernCommandSenderSpigotCreator.stupidDumbHackToMakeTheJvmHappy(this);
        } catch (ClassNotFoundException ignored) {
            //noinspection ConstantConditions
            return null;
        }
    }

    @Override
    public boolean isPermissionSet(String name) {
        return base.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return base.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return base.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return base.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return base.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return base.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return base.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return base.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        base.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        base.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return base.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return base.isOp();
    }

    @Override
    public void setOp(boolean value) {
        base.setOp(value);
    }
}
