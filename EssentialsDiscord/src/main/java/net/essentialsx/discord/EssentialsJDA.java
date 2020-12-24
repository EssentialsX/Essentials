package net.essentialsx.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.essentialsx.discord.listeners.BukkitListener;
import net.essentialsx.discord.listeners.DiscordListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EssentialsJDA {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");
    private final EssentialsDiscord plugin;

    private JDA jda;
    private Guild guild;
    private TextChannel primaryChannel;

    public EssentialsJDA(EssentialsDiscord plugin) {
        this.plugin = plugin;
    }

    public TextChannel getChannel(String key) {
        TextChannel channel = guild.getTextChannelById(plugin.getSettings().getChannelId(key));
        if (channel == null) {
            channel = primaryChannel;
        }
        return channel;
    }

    public void sendMessage(String key, String message) {
        getChannel(key).sendMessage(message).queue();
    }

    public void startup() throws LoginException, InterruptedException {
        shutdown();

        logger.log(Level.INFO, "Attempting to login to discord...");
        if (plugin.getSettings().getBotToken().replace("INSERT-TOKEN-HERE", "").trim().isEmpty()) {
            throw new IllegalArgumentException("No token provided!");
        }

        jda = JDABuilder.createDefault(plugin.getSettings().getBotToken())
                .addEventListeners(new DiscordListener(plugin))
                .setContextEnabled(false)
                .build()
                .awaitReady();
        updatePresence();
        logger.log(Level.INFO, "Successfully logged in as " + jda.getSelfUser().getAsTag());

        guild = jda.getGuildById(plugin.getSettings().getGuildId());
        if (guild == null) {
            throw new IllegalArgumentException("No guild configured!");
        }

        updatePrimaryChannel();

        Bukkit.getPluginManager().registerEvents(new BukkitListener(this), plugin);
    }

    public void updatePrimaryChannel() {
        TextChannel channel = guild.getTextChannelById(plugin.getSettings().getPrimaryChannelId());
        if (channel == null) {
            channel = guild.getDefaultChannel();
        }
        primaryChannel = channel;
    }

    public void updatePresence() {
        String activity = plugin.getSettings().getStatusActivity().trim().toUpperCase();
        if (!activity.equals("DEFAULT") && !activity.equals("LISTENING") && !activity.equals("WATCHING") && !activity.equals("COMPETING")) {
            activity = "DEFAULT";
        }
        jda.getPresence().setActivity(Activity.of(Activity.ActivityType.valueOf(activity), plugin.getSettings().getStatusMessage()));
    }

    public void shutdown() {
        if (jda != null) {
            jda.removeEventListener(jda.getRegisteredListeners());
            HandlerList.unregisterAll(plugin);
            jda.shutdown();
        }
    }

    public EssentialsDiscord getPlugin() {
        return plugin;
    }
}
