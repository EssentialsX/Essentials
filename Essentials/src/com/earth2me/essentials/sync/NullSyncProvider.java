package com.earth2me.essentials.sync;

import net.ess3.api.IUser;

/* 
 * Used in the case that no sync provider is configured.
 */
public class NullSyncProvider implements ISyncProvider {

	@Override
	public void setNickname(IUser user, String nick) {}

	@Override
	public void addMail(IUser user, String message) {}

}