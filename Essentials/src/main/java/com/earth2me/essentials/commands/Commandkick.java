package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.TranslatableException;
import net.essentialsx.api.v2.events.UserKickEvent;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

public class Commandkick extends EssentialsCommand {
    public Commandkick() {
        super("kick");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final User target = getPlayer(server, args, 0, true, false);
        final User user = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null;

        if (user != null) {
            if (target.isHidden(sender.getPlayer()) && !user.canInteractVanished() && target.isHiddenFrom(sender.getPlayer())) {
                throw new PlayerNotFoundException();
            }

            if (target.isAuthorized("essentials.kick.exempt")) {
                throw new TranslatableException("kickExempt");
            }
        }

        String kickReason = args.length > 1 ? getFinalArg(args, 1) : tlLiteral("kickDefault");
        kickReason = FormatUtil.replaceFormat(kickReason.replace("\\n", "\n").replace("|", "\n"));

        final UserKickEvent event = new UserKickEvent(user, target, kickReason);
        ess.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        kickReason = event.getReason();
        target.getBase().kickPlayer(kickReason);
        final String senderDisplayName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.DISPLAY_NAME;

        final String tlKey = "playerKicked";
        final Object[] objects = {senderDisplayName, target.getName(), kickReason};
        ess.getLogger().log(Level.INFO, AdventureUtil.miniToLegacy(tlLiteral(tlKey, objects)));
        ess.broadcastTl(null, "essentials.kick.notify", tlKey, objects);
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
