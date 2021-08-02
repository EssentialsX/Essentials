package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandmsg extends EssentialsLoopCommand {
    public Commandmsg() {
        super("msg");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 1);
        final boolean canWildcard = sender.isAuthorized("essentials.msg.multiple", ess);
        if (sender.isPlayer()) {
            final User user = ess.getUser(sender.getPlayer());
            if (user.isMuted()) {
                final String dateDiff = user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : null;
                if (dateDiff == null) {
                    throw new Exception(user.hasMuteReason() ? tl("voiceSilencedReason", user.getMuteReason()) : tl("voiceSilenced"));
                }
                throw new Exception(user.hasMuteReason() ? tl("voiceSilencedReasonTime", dateDiff, user.getMuteReason()) : tl("voiceSilencedTime", dateDiff));
            }
            message = FormatUtil.formatMessage(user, "essentials.msg", message);
        } else {
            message = FormatUtil.replaceFormat(message);
        }

        // Sending messages to console
        if (args[0].equalsIgnoreCase(Console.NAME) || args[0].equalsIgnoreCase(Console.DISPLAY_NAME)) {
            final IMessageRecipient messageSender = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : Console.getInstance();
            messageSender.sendMessage(Console.getInstance(), message);
            return;
        }

        loopOnlinePlayers(server, sender, false, canWildcard, args[0], new String[] {message});
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User messageReceiver, final String[] args) {
        final IMessageRecipient messageSender = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : Console.getInstance();
        messageSender.sendMessage(messageReceiver, args[0]); // args[0] is the message.
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList(); // It's a chat message, send an empty list.
        }
    }
}
