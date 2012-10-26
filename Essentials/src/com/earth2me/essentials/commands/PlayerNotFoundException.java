package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;

public class PlayerNotFoundException extends NoSuchFieldException
{
	public PlayerNotFoundException()
	{
		super(_("playerNotFound"));
	}
}
