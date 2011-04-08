package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.craftbukkit.inventory.CraftInventory;

public class Commandinvsee extends EssentialsCommand {

	public Commandinvsee() {
		super("invsee");
	}

	@Override
	protected void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception {
		
		if (args.length == 0 && user.savedInventory == null) {
			user.sendMessage("§cUsage: /" + commandLabel + " <user>");
		}
		User invUser = user;
		if (args.length == 1) {
			invUser = getPlayer(server, args, 0);
		}
		if (invUser == user && user.savedInventory != null) {
			((CraftInventory)invUser.getInventory()).setContents(user.savedInventory);
			user.savedInventory = null;
			user.sendMessage("Your inventory has been restored.");
			return;
		}
		
		user.charge(this);
		if (user.savedInventory == null) {
			user.savedInventory = user.getInventory().getContents();
		}
		((CraftInventory)user.getInventory()).setContents(((CraftInventory)invUser.getInventory()).getContents());
		user.sendMessage("You see the inventory of "+invUser.getDisplayName()+".");
		user.sendMessage("Use /invsee to restore your inventory.");
	}
}
