package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FloatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;


public class Commandsetworth extends EssentialsCommand {

    private final Pattern COLON_PATTERN = Pattern.compile(":");

    public Commandsetworth() {
        super("setworth");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        ItemStack stack;
        String price;

        if (args.length == 1) {
            stack = user.getBase().getInventory().getItemInHand();
            price = args[0];
        } else {
            final String stackArg = args[0];
            if(COLON_PATTERN.matcher(stackArg).find()) {
                final String[] split = COLON_PATTERN.split(stackArg);
                if(NumberUtil.isInt(split[1])) {
                    stack = ess.getItemDb().get(split[0]);
                    stack.setDurability(Short.parseShort(split[1]));
                } else throw new NumberFormatException("NaN is not valid");
            } else stack = ess.getItemDb().get(stackArg);
            price = args[1];
        }

        ess.getWorth().setPrice(stack, FloatUtil.parseDouble(price));
        user.sendMessage(tl("worthSet"));
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final ItemStack stack;
        final String stackArg = args[0];
        if(COLON_PATTERN.matcher(stackArg).find()) {
            final String[] split = COLON_PATTERN.split(stackArg);
            if(NumberUtil.isInt(split[1])) {
                stack = ess.getItemDb().get(split[0]);
                stack.setDurability(Short.parseShort(split[1]));
            } else throw new NumberFormatException("NaN is not valid");
        } else stack = ess.getItemDb().get(stackArg);

        ess.getWorth().setPrice(stack, FloatUtil.parseDouble(args[1]));
        sender.sendMessage(tl("worthSet"));
    }
}
