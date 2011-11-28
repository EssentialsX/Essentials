package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.textreader.*;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandhelp extends EssentialsCommand
{
	public Commandhelp()
	{
		super("help");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		IText output;
		String pageStr = args.length > 0 ? args[0] : null;
		String chapterPageStr = args.length > 1 ? args[1] : null;
		final IText input = new TextInput(user, "help", false, ess);

		if (input.getLines().isEmpty())
		{
			if (Util.isInt(pageStr) || pageStr == null)
			{
				output = new HelpInput(user, "", ess);
			}
			else
			{
				output = new HelpInput(user, pageStr, ess);
				pageStr = chapterPageStr;
			}
			chapterPageStr = null;
		}
		else
		{
			output = new KeywordReplacer(input, user, ess);
		}
		final TextPager pager = new TextPager(output);
		pager.showPage(pageStr, chapterPageStr, "help", user);
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		sender.sendMessage(_("helpConsole"));
	}
}
