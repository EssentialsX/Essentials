package com.earth2me.essentials.signs;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import java.util.List;


public class SignMail extends EssentialsSign
{
	public SignMail()
	{
		super("Mail");
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final IUser player, final String username, final IEssentials ess) throws SignException
	{
		final List<String> mail;
		player.acquireReadLock();
		try
		{
			mail = player.getData().getMails();
		}
		finally
		{
			player.unlock();
		}
		if (mail == null || mail.isEmpty())
		{
			player.sendMessage(_("noNewMail"));
			return false;
		}
		for (String s : mail)
		{
			player.sendMessage(s);
		}
		player.sendMessage(_("markMailAsRead"));
		return true;
	}
}
