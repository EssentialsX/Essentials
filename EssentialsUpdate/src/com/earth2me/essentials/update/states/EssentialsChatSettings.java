package com.earth2me.essentials.update.states;

import org.bukkit.entity.Player;


public class EssentialsChatSettings extends AbstractYesNoState
{
	public EssentialsChatSettings(final StateMap states)
	{
		super(states, EssentialsSpawn.class);
	}

	@Override
	public boolean guessAnswer()
	{
		if (getState(AdvancedMode.class).getAnswer())
		{
			setAnswer(false);
			return true;
		}
		return false;
	}

	@Override
	public void askQuestion(final Player sender)
	{
		sender.sendMessage("Would you like to configure EssentialsChat to prefix ingame messages with their group?");
	}
}
