package com.earth2me.essentials.commands;

import com.earth2me.essentials.Backup;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandbackup extends EssentialsCommand
{
	public Commandbackup()
	{
		super("backup");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		Backup backup = Essentials.getBackup();
		if (backup == null)
		{
			return;
		}
		charge(sender);
		backup.run();
		sender.sendMessage(Util.i18n("backupStarted"));
	}
}
