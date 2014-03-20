package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;

public class PlayerNotFoundException extends NoSuchFieldException
{
	public PlayerNotFoundException()
	{
		super(tl("playerNotFound"));
	}
}
