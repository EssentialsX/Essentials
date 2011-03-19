package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.entity.Player;

public class Commandext extends EssentialsCommand {

	public Commandext() {
		super("ext");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception {
		if (args.length < 1) {
			User.charge(user, this);
			user.setFireTicks(0);
			user.sendMessage("§7You extinguished yourself.");
			return;
		}

		for (Player p : server.matchPlayer(args[0])) {
			User.charge(user, this);
			p.setFireTicks(0);
			user.sendMessage("§7You extinguished " + p.getDisplayName() + ".");
		}
	}
}
