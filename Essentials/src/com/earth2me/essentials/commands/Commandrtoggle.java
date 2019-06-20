package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

/**
 * <p>Commandrtoggle class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Commandrtoggle extends EssentialsToggleCommand {
    /**
     * <p>Constructor for Commandrtoggle.</p>
     */
    public Commandrtoggle() {
        super("rtoggle", "essentials.rtoggle.others");
    }

    /** {@inheritDoc} */
    @Override
    public void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        handleToggleWithArgs(server, user, args);
    }

    /** {@inheritDoc} */
    @Override
    public void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        toggleOtherPlayers(server, sender, args);
    }

    @Override
    void togglePlayer(CommandSource sender, User user, Boolean enabled) throws NotEnoughArgumentsException {
        if (enabled == null) {
            enabled = !user.isLastMessageReplyRecipient();
        }

        user.setLastMessageReplyRecipient(enabled);

        user.sendMessage(!enabled ? tl("replyLastRecipientDisabled") : tl("replyLastRecipientEnabled"));
        if (!sender.isPlayer() || !user.getBase().equals(sender.getPlayer())) {
            sender.sendMessage(!enabled ? tl("replyLastRecipientDisabledFor", user.getDisplayName()) : tl("replyLastRecipientEnabledFor", user.getDisplayName()));
        }
    }
}
