package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.utils.FormatUtil;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandbroadcastworld extends EssentialsCommand {

    public Commandbroadcastworld() {
        super("broadcastworld");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        World world = user.getWorld();
        String message = getFinalArg(args, 0);
        if (args.length > 1 && ess.getSettings().isAllowWorldInBroadcastworld()) {
            final World argWorld = ess.getWorld(args[0]);
            if (argWorld != null) {
                world = argWorld;
                message = getFinalArg(args, 1);
            }
        }

        sendBroadcast(world, user.getName(), message);
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException("world");
        }

        final World world = ess.getWorld(args[0]);
        if (world == null) {
            throw new Exception(tl("invalidWorld"));
        }
        sendBroadcast(world, sender.getSender().getName(), getFinalArg(args, 1));
    }

    private void sendBroadcast(final World world, final String name, final String message) throws Exception {
        if (message.isEmpty()) {
            throw new NotEnoughArgumentsException();
        }
        sendToWorld(world, tl("broadcast", FormatUtil.replaceFormat(message).replace("\\n", "\n"), name));
    }

    private void sendToWorld(final World world, final String message) {
        IText broadcast = new SimpleTextInput(message);
        final Collection<Player> players = ess.getOnlinePlayers();

        for (final Player player : players) {
            if (player.getWorld().equals(world)) {
                final User user = ess.getUser(player);
                broadcast = new KeywordReplacer(broadcast, new CommandSource(player), ess, false);
                for (final String messageText : broadcast.getLines()) {
                    user.sendMessage(messageText);
                }
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1 && (!sender.isPlayer() || ess.getSettings().isAllowWorldInBroadcastworld())) {
            final List<String> worlds = Lists.newArrayList();
            for (final World world : server.getWorlds()) {
                worlds.add(world.getName());
            }
            return worlds;
        } else {
            return Collections.emptyList();
        }
    }
}
