package com.earth2me.essentials.update.states;

import com.earth2me.essentials.update.WorkListener;
import com.earth2me.essentials.update.tasks.InstallModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class EssentialsChatSettings extends AbstractYesNoState
{
	public EssentialsChatSettings(final StateMap states)
	{
		super(states, EssentialsSpawn.class);
	}

	@Override
	public void askQuestion(final Player sender)
	{
		sender.sendMessage("Would you like to configure EssentialsChat to prefix ingame messages with their group?");
	}

	@Override
	public void doWork(final WorkListener listener)
	{
		if (getAnswer())
		{
			//TODO: Configure plugin
			
			return;
		}
		listener.onWorkDone();
	}
}
