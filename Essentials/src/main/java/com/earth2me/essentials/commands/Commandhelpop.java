package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;

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
        final String message = sendMessage(server, user.getName(), args);
        if (!user.isAuthorized("essentials.helpop.receive")) {
            user.sendMessage(message);
        }
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sendMessage(server, Console.DISPLAY_NAME, args);
    }

    private String sendMessage(final Server server, final String from, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        final String message = tl("helpOp", from, FormatUtil.stripFormat(getFinalArg(args, 0)));
        ess.getLogger().log(Level.INFO, message);
        ess.broadcastMessage("essentials.helpop.receive", message);
        return message;
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        return Collections.emptyList();
    }
}
