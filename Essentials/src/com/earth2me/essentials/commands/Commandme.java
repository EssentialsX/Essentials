package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;


public class Commandme extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String[] args) throws Exception
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
			message = message.replaceAll("&([0-9a-f])", "§$1");
		}

		ess.broadcastMessage(user, _("action", user.getDisplayName(), message));
	}
}
