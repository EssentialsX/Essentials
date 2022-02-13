package net.essentialsx.discord.util;

import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.provider.providers.BukkitSenderProvider;
import net.ess3.provider.providers.PaperCommandSender;
import net.essentialsx.discord.JDADiscordService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitTask;

public class DiscordCommandSender {
    private final CommandSender sender;
    private BukkitTask task;
    private String responseBuffer = "";
    private long lastTime = System.currentTimeMillis();

    public DiscordCommandSender(JDADiscordService jda, ConsoleCommandSender sender, CmdCallback callback) {
        final BukkitSenderProvider.MessageHook hook = message -> {
            responseBuffer = responseBuffer + (responseBuffer.isEmpty() ? "" : "\n") + MessageUtil.sanitizeDiscordMarkdown(FormatUtil.stripFormat(message));
            lastTime = System.currentTimeMillis();
        };
        this.sender = getCustomSender(sender, hook);

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(jda.getPlugin(), () -> {
            if (!responseBuffer.isEmpty() && System.currentTimeMillis() - lastTime >= 1000) {
                callback.onMessage(responseBuffer);
                responseBuffer = "";
                lastTime = System.currentTimeMillis();
                return;
            }

            if (System.currentTimeMillis() - lastTime >= 20000) {
                task.cancel();
            }
        }, 0, 20);
    }

    private CommandSender getCustomSender(final ConsoleCommandSender consoleSender, final BukkitSenderProvider.MessageHook hook) {
        if (VersionUtil.isPaper() && VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_5_R01)) {
            if (PaperCommandSender.forwardingSenderAvailable()) {
                return PaperCommandSender.createCommandSender(hook::sendMessage);
            }
            return new PaperCommandSender(consoleSender, hook);
        }
        return new BukkitSenderProvider(consoleSender, hook);
    }

    public interface CmdCallback {
        void onMessage(String message);
    }

    public CommandSender getSender() {
        return sender;
    }
}
