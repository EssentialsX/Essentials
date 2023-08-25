package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.utils.FormatUtil;
import net.essentialsx.api.v2.events.HelpopMessageSentEvent;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
        ess.broadcastMessage("essentials.helpop.receive", finalMessage);

        final HelpopMessageSentEvent sentEvent = new HelpopMessageSentEvent(from, getOnlineHelpopRecipients(), message);
        ess.getServer().getPluginManager().callEvent(sentEvent);

        return finalMessage;
    }

    private List<? extends IMessageRecipient> getOnlineHelpopRecipients() {
        return ess.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("essentials.helpop.receive"))
                .map(ess::getUser)
                .collect(Collectors.toList());
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        return Collections.emptyList();
    }
}
