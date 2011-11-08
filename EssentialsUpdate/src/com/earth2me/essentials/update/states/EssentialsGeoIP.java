package com.earth2me.essentials.update.states;

import com.earth2me.essentials.update.AbstractWorkListener;
import com.earth2me.essentials.update.tasks.InstallModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class EssentialsGeoIP extends AbstractYesNoState
{
	public EssentialsGeoIP(final StateMap states)
	{
		super(states, null);
	}

	@Override
	public boolean guessAnswer()
	{
		final Plugin plugin = Bukkit.getPluginManager().getPlugin("EssentialsGeoIP");
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
		sender.sendMessage("Do you want to install EssentialsGeoIP? (yes/no)");
		sender.sendMessage("EssentialsGeoIP performs a IP lookup on joining players");
		sender.sendMessage("It allows you get a rough idea of where a player is from.");
	}

	@Override
	public void doWork(final AbstractWorkListener listener)
	{
		if (getAnswer())
		{
			new InstallModule(listener, "EssentialsGeoIP").start();
			return;
		}
		listener.onWorkDone();
	}
}