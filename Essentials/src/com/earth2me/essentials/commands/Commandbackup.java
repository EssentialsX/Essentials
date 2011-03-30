/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.earth2me.essentials.commands;

import com.earth2me.essentials.Backup;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

/**
 *
 * @author schlex
 */
public class Commandbackup extends EssentialsCommand {

	public Commandbackup() {
		super("backup");
	}

	@Override
	protected void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception {
		Backup backup = Essentials.getStatic().backup;
		if (backup == null) return;
		backup.run();
	}

	@Override
	protected void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception {
		Backup backup = Essentials.getStatic().backup;
		if (backup == null) return;
		user.charge(this);
		backup.run();
		user.sendMessage("Backup started");
	}
	
	

	
}
