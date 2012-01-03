package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IBackup;
import org.bukkit.command.CommandSender;


public class Commandbackup extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String[] args) throws Exception
	{
		final IBackup backup = ess.getBackup();
		backup.run();
		sender.sendMessage(_("backupStarted"));
	}
}
