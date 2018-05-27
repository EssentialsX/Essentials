package com.earth2me.essentials.sync;

import com.earth2me.essentials.UserData;

/* 
 * Used in the case that no sync provider is configured.
 */
public class NullSyncProvider implements ISyncProvider {

	@Override
	public void addMail(UserData user, String message) {}

	@Override
	public void setNickname(UserData user, String nick) {}

}