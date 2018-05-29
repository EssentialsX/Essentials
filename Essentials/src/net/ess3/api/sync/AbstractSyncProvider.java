package net.ess3.api.sync;

import com.earth2me.essentials.UserData;

/* 
 * Abstract provider from which sync providers can be extended.
 */
public abstract class AbstractSyncProvider implements ISyncProvider {

	@Override
	public void addMail(UserData user, String message) {}

	@Override
	public void clearMail(UserData user) {}

	@Override
	public void setNickname(UserData user, String nick) {}

	@Override
	public void setTeleport(UserData user, boolean state) {}

	@Override
	public void setMuted(UserData user, boolean state) {}

	@Override
	public void setMuteTimeout(UserData user, long time) {}

}