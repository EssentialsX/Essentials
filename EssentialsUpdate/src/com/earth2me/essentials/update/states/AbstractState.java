package com.earth2me.essentials.update.states;

import com.earth2me.essentials.update.AbstractWorkListener;
import org.bukkit.entity.Player;


public abstract class AbstractState
{
	private transient boolean abortion = false;
	private final transient StateMap stateMap;

	public AbstractState(final StateMap stateMap)
	{
		this.stateMap = stateMap;
	}

	public <T extends AbstractState> T getState(final Class<? extends T> stateClass)
	{
		if (!stateMap.containsKey(stateClass))
		{
			try
			{
				final AbstractState state = stateClass.getConstructor(StateMap.class).newInstance(stateMap);
				stateMap.put(stateClass, state);
			}
			catch (Exception ex)
			{
				/*
				 * This should never happen. All states, that are added to the map automatically, have to have a
				 * Constructor that accepts the StateMap.
				 */
				throw new RuntimeException(ex);
			}
		}
		return (T)stateMap.get(stateClass);
	}

	public abstract AbstractState getNextState();

	/**
	 * Check if we already know the answer, so the user does not have to answer the question.
	 * 
	 * @return true, if the answer could be guessed.
	 */
	public boolean guessAnswer()
	{
		return false;
	}

	/**
	 * Ask the user the question.
	 * @param sender 
	 */
	public abstract void askQuestion(Player sender);

	/**
	 * React on the answer and set internal variables
	 * @param answer
	 * @return true, if the answer could be recognized as a valid answer
	 */
	public abstract boolean reactOnAnswer(String answer);

	public final AbstractState reactOnAnswer(final Player sender, final String answer)
	{
		final String trimmedAnswer = answer.trim();
		if (trimmedAnswer.equalsIgnoreCase("quit")
			|| trimmedAnswer.equalsIgnoreCase("bye")
			|| trimmedAnswer.equalsIgnoreCase("abort")
			|| trimmedAnswer.equalsIgnoreCase("cancel")
			|| trimmedAnswer.equalsIgnoreCase("exit"))
		{
			abort();
			return null;
		}
		try
		{
			final boolean found = reactOnAnswer(trimmedAnswer);
			if (found)
			{
				return getNextState();
			}
			else
			{
				sender.sendMessage("Answer not recognized.");
				return this;
			}
		}
		catch (RuntimeException ex)
		{
			sender.sendMessage(ex.toString());
			return this;
		}
	}

	/**
	 * Do something based on the answer, that the user gave.
	 */
	public void doWork(final AbstractWorkListener listener)
	{
		listener.onWorkDone();
	}

	public boolean isAbortion()
	{
		return abortion;
	}

	protected void abort()
	{
		abortion = true;
	}
}
