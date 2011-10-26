package com.earth2me.essentials.update.states;

import com.earth2me.essentials.update.AbstractWorkListener;
import com.earth2me.essentials.update.tasks.InstallModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class EssentialsSpawn extends AbstractYesNoState
{
	public EssentialsSpawn(final StateMap states)
	{
		super(states, null);
	}

	@Override
	public boolean guessAnswer()
	{
		final Plugin plugin = Bukkit.getPluginManager().getPlugin("EssentialsSpawn");
		if (plugin != null)
		{
			setAnswer(true);
			return true;
		}
		return false;
	}

	@Override
	public void askQuestion(final Player sender)
	{
		sender.sendMessage("Do you want to install EssentialsSpawn? (yes/no)");
		sender.sendMessage("EssentialsSpawn lets you control player spawning");
		sender.sendMessage("It allows you to set different places where players spawn on death, new players join and allows players to return to spawn.");
	}

	@Override
	public void doWork(final AbstractWorkListener listener)
	{
		if (getAnswer())
		{
			new InstallModule(listener, "EssentialsSpawn").start();
			return;
		}
		listener.onWorkDone();
	}
}