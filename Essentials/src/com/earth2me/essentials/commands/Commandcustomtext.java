package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.TextInput;
import com.earth2me.essentials.textreader.TextPager;
import org.bukkit.Server;


public class Commandcustomtext extends EssentialsCommand
{
	public Commandcustomtext()
	{
		super("customtext");
	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		final IText input = new TextInput(sender, "custom", true, ess);
		final IText output = new KeywordReplacer(input, sender, ess);
		final TextPager pager = new TextPager(output);
		pager.showPage(commandLabel, args.length > 0 ? args[0] : null, null, sender);
	}
}
