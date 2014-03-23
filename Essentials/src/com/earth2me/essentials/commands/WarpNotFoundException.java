package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;

public class WarpNotFoundException extends Exception
{
	public WarpNotFoundException()
	{
		super(tl("warpNotExist"));
	}
	
	public WarpNotFoundException(String message)
	{
		super(message);
	}
}
