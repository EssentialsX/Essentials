package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.IReplyTo;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;


public class Commandr extends EssentialsCommand {
    public Commandr() {
        super("r");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        IReplyTo replyTo;
        String senderName;

        if (sender.isPlayer()) {
            User user = ess.getUser(sender.getPlayer());
            message = FormatUtil.formatMessage(user, "essentials.msg", message);
            replyTo = user;
            senderName = user.getDisplayName();
        } else {
            message = FormatUtil.replaceFormat(message);
            replyTo = Console.getConsoleReplyTo();
            senderName = Console.NAME;
        }

        final CommandSource target = replyTo.getReplyTo();

        if (target == null || (target.isPlayer() && !target.getPlayer().isOnline())) {
            throw new Exception(tl("foreverAlone"));
        }

        final String targetName = target.isPlayer() ? target.getPlayer().getDisplayName() : Console.NAME;

        sender.sendMessage(tl("msgFormat", tl("me"), targetName, message));
        if (target.isPlayer()) {
            User player = ess.getUser(target.getPlayer());
            if (sender.isPlayer() && player.isIgnoredPlayer(ess.getUser(sender.getPlayer()))) {
                return;
            }
        }
        target.sendMessage(tl("msgFormat", senderName, tl("me"), message));
        replyTo.setReplyTo(target);
        if (target != sender) {
            if (target.isPlayer()) {
                ess.getUser(target.getPlayer()).setReplyTo(sender);
            } else {
                Console.getConsoleReplyTo().setReplyTo(sender);
            }
        }
    }
}
