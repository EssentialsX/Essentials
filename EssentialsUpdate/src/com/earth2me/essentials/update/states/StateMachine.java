package com.earth2me.essentials.update.states;

import com.earth2me.essentials.update.UpdateCheck;
import com.earth2me.essentials.update.AbstractWorkListener;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class StateMachine extends AbstractWorkListener implements Runnable
{
	public enum MachineResult
	{
		ABORT, WAIT, DONE, NONE
	}
	private final transient StateMap states = new StateMap();
	private transient AbstractState current;
	private transient Player player;
	private transient MachineResult result = MachineResult.NONE;

	public StateMachine(final Plugin plugin, final Player player, final UpdateCheck updateCheck)
	{
		super(plugin, updateCheck.getNewVersionInfo());
		this.player = player;
		states.clear();
		final UpdateOrInstallation state = new UpdateOrInstallation(states, updateCheck);
		current = states.put(UpdateOrInstallation.class, state);
	}

	public MachineResult askQuestion()
	{
		try
		{
			while (current.guessAnswer())
			{
				current = current.getNextState();
				if (current == null)
				{
					result = MachineResult.DONE;
					break;
				}
			}
			if (current != null)
			{
				if (player.isOnline())
				{
					current.askQuestion(player);
				}
				result = MachineResult.WAIT;
			}
		}
		catch (RuntimeException ex)
		{
			player.sendMessage(ex.getMessage());
			finish();
			result = MachineResult.ABORT;
		}
		return result;
	}

	public MachineResult reactOnMessage(final String message)
	{
		result = MachineResult.NONE;
		final AbstractState next = current.reactOnAnswer(player, message);
		if (next == null)
		{
			if (current.isAbortion())
			{
				finish();
				result = MachineResult.ABORT;
			}
			else
			{
				result = MachineResult.DONE;
			}
		}
		else
		{
			current = next;
			askQuestion();
		}
		return result;
	}
	private transient Iterator<AbstractState> iterator;

	public void startWork()
	{
		iterator = states.values().iterator();
		Bukkit.getScheduler().scheduleAsyncDelayedTask(getPlugin(), this);
	}

	@Override
	public void run()
	{
		if (!iterator.hasNext())
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable()
			{
				@Override
				public void run()
				{
					if (StateMachine.this.player.isOnline())
					{
						StateMachine.this.player.sendMessage("Installation done. Reloading server.");
					}
					finish();
					Bukkit.getServer().reload();
				}
			});
			return;
		}
		final AbstractState state = iterator.next();
		state.doWork(this);
	}

	@Override
	public void onWorkAbort(final String message)
	{
		finish();
		Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				if (message != null && !message.isEmpty() && StateMachine.this.player.isOnline())
				{
					StateMachine.this.player.sendMessage(message);
				}
			}
		});
	}

	@Override
	public void onWorkDone(final String message)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				if (message != null && !message.isEmpty() && StateMachine.this.player.isOnline())
				{
					StateMachine.this.player.sendMessage(message);
				}
				Bukkit.getScheduler().scheduleAsyncDelayedTask(getPlugin(), StateMachine.this);
			}
		});
	}

	private void finish()
	{
		current = null;
		iterator = null;
		states.clear();
		getPlugin().getServer().getPluginManager().callEvent(new InstallationFinishedEvent());
	}

	public void resumeInstallation(final Player player)
	{
		this.player = player;
		if (result == MachineResult.WAIT)
		{
			if (current == null)
			{
				throw new RuntimeException("State is WAIT, but current state is null!");
			}
			current.askQuestion(player);
		}
		if (result == MachineResult.DONE && iterator != null)
		{
			player.sendMessage("Installation is still running.");
		}
		if (result == MachineResult.ABORT)
		{
			throw new RuntimeException("Player should not be able to resume an aborted installation.");
		}
		if (result == MachineResult.NONE)
		{
			throw new RuntimeException("State machine in an undefined state.");
		}
	}
}
