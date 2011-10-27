package com.earth2me.essentials.update.states;

import org.bukkit.entity.Player;


public class AdvancedMode extends AbstractYesNoState
{
	public AdvancedMode(final StateMap states)
	{
		super(states, EssentialsChat.class);
	}

	@Override
	public void askQuestion(final Player sender)
	{
		sender.sendMessage("This installation mode has a lot of options.");
		sender.sendMessage("Do you want use the advanced mode to see all questions?");
		sender.sendMessage("Otherwise the default values will be used.");
	}
}
