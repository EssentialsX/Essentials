package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;

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
        sendMessage(user, server, user.getDisplayName(), args);
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sendMessage(null, server, Console.DISPLAY_NAME, args);
    }

    private void sendMessage(final IUser user, final Server server, final String from, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        final String message = FormatUtil.stripFormat(getFinalArg(args, 0));
        ess.getLogger().log(Level.INFO, tlLiteral("helpOp", from, message));
        ess.broadcastTl(null, u -> !u.isAuthorized("essentials.helpop.receive") && !u.equals(user), "helpOp", from, message);
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        return Collections.emptyList();
    }
}
