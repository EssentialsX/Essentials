package com.neximation.essentials.commands;

import com.neximation.essentials.CommandSource;
import com.neximation.essentials.textreader.IText;
import com.neximation.essentials.textreader.KeywordReplacer;
import com.neximation.essentials.textreader.TextInput;
import com.neximation.essentials.textreader.TextPager;
import org.bukkit.Server;


public class Commandrules extends EssentialsCommand {
    public Commandrules() {
        super("rules");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (sender.isPlayer()) {
            ess.getUser(sender.getPlayer()).setDisplayNick();
        }

        final IText input = new TextInput(sender, "rules", true, ess);
        final IText output = new KeywordReplacer(input, sender, ess);
        final TextPager pager = new TextPager(output);
        pager.showPage(args.length > 0 ? args[0] : null, args.length > 1 ? args[1] : null, commandLabel, sender);
    }
}
