package net.essentialsx.discord.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.essentialsx.discord.JDADiscordService;
import net.essentialsx.discord.util.DiscordUtil;
import net.essentialsx.discord.util.MessageUtil;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commanddiscordbroadcast extends EssentialsCommand {
    public Commanddiscordbroadcast() {
        super("discordbroadcast");
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        if (!sender.isAuthorized("essentials.discordbroadcast." + args[0], ess)) {
            throw new Exception(tl("discordbroadcastPermission", args[0]));
        }

        String message = getFinalArg(args, 1);
        if (!sender.isAuthorized("essentials.discordbroadcast.markdown", ess)) {
            message = MessageUtil.sanitizeDiscordMarkdown(message);
        }

        final JDADiscordService jda = (JDADiscordService) module;
        final TextChannel channel = jda.getDefinedChannel(args[0], false);
        if (channel == null) {
            throw new Exception(tl("discordbroadcastInvalidChannel", args[0]));
        }

        if (!channel.canTalk()) {
            throw new Exception(tl("discordNoSendPermission", channel.getName()));
        }

        channel.sendMessage(jda.parseMessageEmotes(message))
                .setAllowedMentions(sender.isAuthorized("essentials.discordbroadcast.ping", ess) ? null : DiscordUtil.NO_GROUP_MENTIONS)
                .queue();

        sender.sendMessage(tl("discordbroadcastSent", "#" + EmojiParser.parseToAliases(channel.getName())));
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            final JDADiscordService jda = (JDADiscordService) module;
            final List<String> channels = jda.getSettings().getChannelNames();
            channels.removeIf(s -> !sender.isAuthorized("essentials.discordbroadcast." + s, ess));
            return channels;
        } else {
            final String curArg = args[args.length - 1];
            if (!curArg.isEmpty() && curArg.charAt(0) == ':' && (curArg.length() == 1 || curArg.charAt(curArg.length() - 1) != ':')) {
                final JDADiscordService jda = (JDADiscordService) module;
                if (jda.getGuild().getEmojiCache().isEmpty()) {
                    return Collections.emptyList();
                }

                final List<String> completions = new ArrayList<>();
                for (final RichCustomEmoji emote : jda.getGuild().getEmojiCache()) {
                    completions.add(":" + emote.getName() + ":");
                }
                return completions;
            }
        }
        return Collections.emptyList();
    }
}
