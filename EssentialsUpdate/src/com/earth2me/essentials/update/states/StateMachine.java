package com.earth2me.essentials.update.states;

import com.earth2me.essentials.update.WorkListener;
import com.earth2me.essentials.update.VersionInfo;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class StateMachine extends WorkListener
{
	public enum MachineResult
	{
		ABORT, WAIT, DONE
	}
	private final transient List<AbstractState> states = new ArrayList<AbstractState>();
	private transient AbstractState current;
	private final transient Player player;

	public StateMachine(final Plugin plugin, final Player player, final VersionInfo newVersionInfo)
	{
		super(plugin, newVersionInfo);
		this.player = player;
		states.clear();
		final AbstractState state = new EssentialsChat(null);
		states.add(state);
		current = state;
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
	private int position = 0;

	public void startWork()
	{
		callStateWork();
	}

	private void callStateWork()
	{
		if (position > states.size())
		{
			if (player.isOnline())
			{
				player.sendMessage("Installation done.");
			}
			return;
		}
		final AbstractState state = states.get(position);
		state.doWork(this);
	}

	@Override
	public void onWorkAbort(final String message)
	{
		position = 0;
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
		position++;
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
