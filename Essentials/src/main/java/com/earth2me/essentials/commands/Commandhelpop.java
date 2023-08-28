package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.utils.FormatUtil;
import net.essentialsx.api.v2.events.HelpopMessageSendEvent;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class Commandhelpop extends EssentialsCommand {
    public Commandhelpop() {
        super("helpop");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        user.setDisplayNick();
        final String message = sendMessage(server, user, args);
        if (!user.isAuthorized("essentials.helpop.receive")) {
            user.sendMessage(message);
        }
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sendMessage(server, Console.getInstance(), args);
    }

    private String sendMessage(final Server server, final IMessageRecipient from, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final String message = FormatUtil.stripFormat(getFinalArg(args, 0));
        final String finalMessage = tl("helpOp", from.getDisplayName(), message);
        ess.getLogger().log(Level.INFO, finalMessage);

        final List<IMessageRecipient> recipients = new ArrayList<>();
        for (User user : ess.getOnlineUsers()) {
            if (user.getBase().hasPermission("essentials.helpop.receive")) {
                recipients.add(user);
            }
        }

        final HelpopMessageSendEvent sendEvent = new HelpopMessageSendEvent(from, recipients, message);
        ess.getServer().getPluginManager().callEvent(sendEvent);

        sendEvent.getRecipients().forEach(recipient -> recipient.sendMessage(finalMessage));

        return finalMessage;
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        return Collections.emptyList();
    }
}
