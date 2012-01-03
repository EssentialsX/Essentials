package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;


public class Commandsuicide extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String[] args) throws Exception
	{
		user.setHealth(0);
		user.sendMessage(_("suicideMessage"));
		ess.broadcastMessage(user,
							 _("suicideSuccess", user.getDisplayName()));
	}
}
