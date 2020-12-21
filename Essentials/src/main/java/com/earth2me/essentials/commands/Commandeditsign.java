package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandeditsign extends EssentialsCommand {
    public Commandeditsign() {
        super("editsign");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0 || (args.length > 1 && !NumberUtil.isInt(args[1]))) {
            throw new NotEnoughArgumentsException();
        }

        final Block target = user.getTargetBlock(5); //5 is a good number
        if (!(target.getState() instanceof Sign)) {
            throw new Exception(tl("editsignCommandTarget"));
        }
        final Sign sign = (Sign) target.getState();
        try {
            if (args[0].equalsIgnoreCase("set") && args.length > 2) {
                final int line = Integer.parseInt(args[1]) - 1;
                final String text = FormatUtil.formatString(user, "essentials.editsign", getFinalArg(args, 2)).trim();
                if (ChatColor.stripColor(text).length() > 15 && !user.isAuthorized("essentials.editsign.unlimited")) {
                    throw new Exception(tl("editsignCommandLimit"));
                }
                sign.setLine(line, text);
                sign.update();
                user.sendMessage(tl("editsignCommandSetSuccess", line + 1, text));
            } else if (args[0].equalsIgnoreCase("clear")) {
                if (args.length == 1) {
                    for (int i = 0; i < 4; i++) { // A whole one line of line savings!
                        sign.setLine(i, "");
                    }
                    sign.update();
                    user.sendMessage(tl("editsignCommandClear"));
                } else {
                    final int line = Integer.parseInt(args[1]) - 1;
                    sign.setLine(line, "");
                    sign.update();
                    user.sendMessage(tl("editsignCommandClearLine", line + 1));
                }
            } else {
                throw new NotEnoughArgumentsException();
            }
        } catch (final IndexOutOfBoundsException e) {
            throw new Exception(tl("editsignCommandNoLine"));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("set", "clear");
        } else if (args.length == 2) {
            return Lists.newArrayList("1", "2", "3", "4");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set") && NumberUtil.isPositiveInt(args[1])) {
            final int line = Integer.parseInt(args[1]);
            final Block target = user.getTargetBlock(5);
            if (target.getState() instanceof Sign && line <= 4) {
                final Sign sign = (Sign) target.getState();
                return Lists.newArrayList(FormatUtil.unformatString(user, "essentials.editsign", sign.getLine(line - 1)));
            }
            return Collections.emptyList();
        } else {
            return Collections.emptyList();
        }
    }
}
