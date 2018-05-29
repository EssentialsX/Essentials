package net.ess3.api.sync;

import com.earth2me.essentials.User;

/* 
 * Abstract provider from which sync providers can be extended.
 */
public abstract class AbstractSyncProvider implements ISyncProvider {

	@Override
	public void addMail(User user, String message) {
	}

	@Override
	public void clearMail(User user) {
	}

	@Override
	public void setNickname(User user, String nick) {
	}

}