package com.earth2me.essentials.update.states;

import com.earth2me.essentials.update.WorkListener;
import com.earth2me.essentials.update.VersionInfo;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class StateMachine extends WorkListener
{
	public enum MachineResult
	{
		ABORT, WAIT, DONE
	}
	private final transient StateMap states = new StateMap();
	private transient AbstractState current;
	private final transient Player player;

	public StateMachine(final Plugin plugin, final Player player, final VersionInfo newVersionInfo)
	{
		super(plugin, newVersionInfo);
		this.player = player;
		states.clear();
		states.add(new EssentialsChat(states));
		states.add(new EssentialsSpawn(states));
		states.add(new EssentialsProtect(states));
		states.add(new EssentialsGeoIP(states));
		current = states.values().iterator().next();
	}

	public MachineResult askQuestion()
	{
		while (current.guessAnswer())
		{
			current = current.getNextState();
			if (current == null)
			{
				return MachineResult.DONE;
			}
		}
		current.askQuestion(player);
		return MachineResult.WAIT;
	}

	public MachineResult reactOnMessage(final String message)
	{
		final AbstractState next = current.reactOnAnswer(player, message);
		if (next == null)
		{
			if (current.isAbortion())
			{
				return MachineResult.ABORT;
			}
			else
			{
				return MachineResult.DONE;
			}
		}
		current = next;
		return askQuestion();
	}
	private transient Iterator<AbstractState> iterator;

	public void startWork()
	{
		iterator = states.values().iterator();
		callStateWork();
	}

	private void callStateWork()
	{
		if (!iterator.hasNext())
		{
			if (player.isOnline())
			{
				player.sendMessage("Installation done.");
			}
			return;
		}
		final AbstractState state = iterator.next();
		state.doWork(this);
	}

	@Override
	public void onWorkAbort(final String message)
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
				StateMachine.this.callStateWork();
			}
		});
	}
}
