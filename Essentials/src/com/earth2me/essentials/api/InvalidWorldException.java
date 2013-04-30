package com.earth2me.essentials.api;

import static com.earth2me.essentials.I18n._;


public class InvalidWorldException extends Exception
{
	private final String world;

	public InvalidWorldException(final String world)
	{
		super(_("invalidWorld"));
		this.world = world;
	}

	public String getWorld()
	{
		return this.world;
	}
}
