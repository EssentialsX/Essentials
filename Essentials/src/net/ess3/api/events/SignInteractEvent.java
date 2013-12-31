package net.ess3.api.events;

import com.earth2me.essentials.signs.EssentialsSign;
import net.ess3.api.IUser;


public class SignInteractEvent extends SignEvent
{
	public SignInteractEvent(EssentialsSign.ISign sign, EssentialsSign essSign, IUser user)
	{
		super(sign, essSign, user);
	}
}
