package com.earth2me.essentials.api;

import com.earth2me.essentials.Util;

public class NoLoanPermittedException extends Exception
{

	public NoLoanPermittedException()
	{
		super(Util.i18n("negativeBalanceError"));
	}
	
}
