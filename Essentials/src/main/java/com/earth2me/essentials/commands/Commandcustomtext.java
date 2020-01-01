package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.TextInput;
import com.earth2me.essentials.textreader.TextPager;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.Server;


public class Commandcustomtext extends EssentialsCommand {
    public Commandcustomtext() {
        super("customtext");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (sender.isPlayer()) {
            ess.getUser(sender.getPlayer()).setDisplayNick();
        }

        final IText input = new TextInput(sender, "custom", true, ess);
        final IText output = new KeywordReplacer(input, sender, ess);
        final TextPager pager = new TextPager(output);
        String chapter = commandLabel;
        String page;

        if (commandLabel.equalsIgnoreCase("customtext") && args.length > 0 && !NumberUtil.isInt(commandLabel)) {
            chapter = args[0];
            page = args.length > 1 ? args[1] : null;
        } else {
            page = args.length > 0 ? args[0] : null;
        }

        pager.showPage(chapter, page, null, sender);
    }
}
