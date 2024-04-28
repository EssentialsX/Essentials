package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.IUser;
import net.essentialsx.api.v2.events.HelpopMessageSendEvent;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

public class Commandhelpop extends EssentialsCommand {
    public Commandhelpop() {
        super("helpop");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        user.setDisplayNick();
        sendMessage(user, args);
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sendMessage(Console.getInstance(), args);
    }

    private void sendMessage(final IMessageRecipient from, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final String message = FormatUtil.stripFormat(getFinalArg(args, 0));
        ess.getLogger().log(Level.INFO, AdventureUtil.miniToLegacy(tlLiteral("helpOp", from.getDisplayName(), message)));

        final List<IUser> recipients = new ArrayList<>();
        for (IUser user : ess.getOnlineUsers()) {
            if (user.getBase().hasPermission("essentials.helpop.receive")) {
                recipients.add(user);
            }
        }

        final HelpopMessageSendEvent sendEvent = new HelpopMessageSendEvent(from, recipients, message);
        ess.getServer().getPluginManager().callEvent(sendEvent);

        from.sendTl("helpOp", from.getDisplayName(), message);

        for (IUser recipient : sendEvent.getRecipients()) {
            recipient.sendTl("helpOp", from.getDisplayName(), message);
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        return Collections.emptyList();
    }
}
