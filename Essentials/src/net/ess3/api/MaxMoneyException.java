package net.ess3.api;

import static com.earth2me.essentials.I18n._;


public class MaxMoneyException extends Exception
{
	public MaxMoneyException()
	{
		super(_("maxMoney"));
	}
}
