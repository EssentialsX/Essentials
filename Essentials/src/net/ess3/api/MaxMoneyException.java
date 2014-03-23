package net.ess3.api;

import static com.earth2me.essentials.I18n.tl;


public class MaxMoneyException extends Exception
{
	public MaxMoneyException()
	{
		super(tl("maxMoney"));
	}
}
