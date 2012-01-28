package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IUser;


public class Commandme extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (user.getData().isMuted())
		{
			throw new Exception(_("voiceSilenced"));
		}

		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		String message = getFinalArg(args, 0);
		if (user.isAuthorized("essentials.chat.color"))
		{
			message = Util.replaceColor(message);
		}
		else {
			message = Util.stripColor(message);
		}
		

		ess.broadcastMessage(user, _("action", user.getDisplayName(), message));
	}
}
