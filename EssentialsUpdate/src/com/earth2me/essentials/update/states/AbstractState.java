package com.earth2me.essentials.update.states;

import com.earth2me.essentials.update.WorkListener;
import org.bukkit.entity.Player;


public abstract class AbstractState
{
	private transient boolean abortion = false;

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
			|| trimmedAnswer.equalsIgnoreCase("abort"))
		{
			abortion = true;
			return null;
		}
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

	/**
	 * Do something based on the answer, that the user gave.
	 */
	public abstract void doWork(WorkListener workListener);

	public boolean isAbortion()
	{
		return abortion;
	}
}
