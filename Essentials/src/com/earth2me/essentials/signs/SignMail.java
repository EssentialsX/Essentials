package com.earth2me.essentials.signs;

import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import java.util.List;
import net.ess3.api.IEssentials;


public class SignMail extends EssentialsSign
{
	public SignMail()
	{
		super("Mail");
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		final List<String> mail = player.getMails();
		if (mail.isEmpty())
		{
			player.sendMessage(tl("noNewMail"));
			return false;
		}
		for (String s : mail)
		{
			player.sendMessage(s);
		}
		player.sendMessage(tl("markMailAsRead"));
		return true;
	}
}
