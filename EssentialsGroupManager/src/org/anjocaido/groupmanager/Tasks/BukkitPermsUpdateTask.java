package org.anjocaido.groupmanager.Tasks;

import org.anjocaido.groupmanager.GroupManager;

/*
 * 
 * Created by ElgarL
 */

public class BukkitPermsUpdateTask implements Runnable {

	public BukkitPermsUpdateTask() {

		super();
	}

	@Override
	public void run() {

		// Signal loaded and update BukkitPermissions.
		GroupManager.setLoaded(true);
		GroupManager.BukkitPermissions.collectPermissions();
		GroupManager.BukkitPermissions.updateAllPlayers();

		GroupManager.logger.info("Bukkit Permissions Updated!");

	}

}