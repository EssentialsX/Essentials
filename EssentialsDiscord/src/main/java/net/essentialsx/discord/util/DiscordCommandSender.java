package net.essentialsx.discord.util;

import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.provider.providers.BukkitSenderProvider;
import net.ess3.provider.providers.PaperCommandSender;
import net.essentialsx.discord.JDADiscordService;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitTask;

public class DiscordCommandSender {
    private final BukkitSenderProvider sender;
    private BukkitTask task;
    private String responseBuffer = "";
    private long lastTime = System.currentTimeMillis();

    public DiscordCommandSender(JDADiscordService jda, ConsoleCommandSender sender, CmdCallback callback) {
        final BukkitSenderProvider.MessageHook hook = message -> {
            responseBuffer = responseBuffer + (responseBuffer.isEmpty() ? "" : "\n") + MessageUtil.sanitizeDiscordMarkdown(FormatUtil.stripFormat(message));
            lastTime = System.currentTimeMillis();
        };
        this.sender = (VersionUtil.isPaper() && VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_5_R01)) ? new PaperCommandSender(sender, hook) : new BukkitSenderProvider(sender, hook);

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

    public interface CmdCallback {
        void onMessage(String message);
    }

    public BukkitSenderProvider getSender() {
        return sender;
    }
}
