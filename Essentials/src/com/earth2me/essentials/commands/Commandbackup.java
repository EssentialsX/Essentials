package com.earth2me.essentials.commands;

import com.earth2me.essentials.api.IBackup;
import static com.earth2me.essentials.I18n._;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandbackup extends EssentialsCommand
{
	public Commandbackup()
	{
		super("backup");
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		final IBackup backup = ess.getBackup();
		backup.run();
		sender.sendMessage(_("backupStarted"));
	}
}
