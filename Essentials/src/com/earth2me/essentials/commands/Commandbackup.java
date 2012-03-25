package com.earth2me.essentials.commands;

import com.earth2me.essentials.Backup;
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
		final Backup backup = ess.getBackup();
		if (backup == null)
		{
			throw new Exception(_("backupDisabled"));
		}
		final String command = ess.getSettings().getBackupCommand();
		if (command == null || "".equals(command) || "save-all".equalsIgnoreCase(command))
		{
			throw new Exception(_("backupDisabled"));
		}
		backup.run();
		sender.sendMessage(_("backupStarted"));
	}
}
