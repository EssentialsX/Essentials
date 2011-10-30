package com.earth2me.essentials.update.states;

import com.earth2me.essentials.update.AbstractWorkListener;
import com.earth2me.essentials.update.tasks.InstallModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class EssentialsProtect extends AbstractYesNoState
{
	public EssentialsProtect(final StateMap states)
	{
		super(states, null);
	}

	@Override
	public boolean guessAnswer()
	{
		final Plugin plugin = Bukkit.getPluginManager().getPlugin("EssentialsProtect");
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
		sender.sendMessage("Do you want to install EssentialsProtect? (yes/no)");
		sender.sendMessage("EssentialsProtect is a basic world protection system");
		sender.sendMessage("It allows you to set server wide rules, such as disabling creeper explosions, and preventing fire spread.");
	}

	@Override
	public void doWork(final AbstractWorkListener listener)
	{
		if (getAnswer())
		{
			new InstallModule(listener, "EssentialsProtect").start();
			return;
		}
		listener.onWorkDone();
	}
}