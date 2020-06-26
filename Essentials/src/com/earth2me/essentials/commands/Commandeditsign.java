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
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        if (args.length < 2 || !NumberUtil.isInt(args[1])) {
            throw new NotEnoughArgumentsException();
        }

        Block target = user.getBase().getTargetBlock(null, 5); //5 is a good number
        if (!(target.getState() instanceof Sign)) {
            throw new Exception(tl("editsignCommandTarget"));
        }
        Sign sign = (Sign) target.getState();
        int line = Integer.parseInt(args[1]) - 1;
        try {
            if (args[0].equalsIgnoreCase("set") && args.length > 2) {
                String text = FormatUtil.formatString(user, "essentials.editsign", getFinalArg(args, 2)).trim();
                if (ChatColor.stripColor(text).length() > 15 && user.isAuthorized("essentials.editsign.unlimited")) {
                    throw new Exception(tl("editsignCommandLimit"));
                }
                sign.setLine(line, text);
                sign.update();
                user.sendMessage(tl("editsignCommandSetSuccess", line + 1, text));
            } else if (args[0].equalsIgnoreCase("reset")) {
                sign.setLine(line, "");
                sign.update();
                user.sendMessage(tl("editsignCommandResetLine", line + 1));
            } else {
                throw new NotEnoughArgumentsException();
            }
        } catch (IndexOutOfBoundsException e) {
            throw new Exception(tl("editsignCommandNoLine"));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("set", "reset");
        } else if (args.length == 2) {
            return Lists.newArrayList("1", "2", "3", "4");
        } else {
            return Collections.emptyList();
        }
    }
}
