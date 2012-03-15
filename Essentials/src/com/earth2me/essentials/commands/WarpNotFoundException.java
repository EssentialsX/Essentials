package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;

public class WarpNotFoundException extends Exception
{
	public WarpNotFoundException()
	{
		super(_("warpNotExist"));
	}
	
	public WarpNotFoundException(String message)
	{
		super(message);
	}
}
