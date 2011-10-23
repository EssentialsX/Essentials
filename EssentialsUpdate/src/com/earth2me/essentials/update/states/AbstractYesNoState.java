package com.earth2me.essentials.update.states;


public abstract class AbstractYesNoState extends AbstractState
{
	private boolean answer = false;
	private final transient AbstractState yesState;
	private final transient AbstractState noState;

	public AbstractYesNoState(final AbstractState yesState, final AbstractState noState)
	{
		this.yesState = yesState;
		this.noState = noState;
	}

	@Override
	public AbstractState getNextState()
	{
		return answer ? yesState : noState;
	}

	@Override
	public boolean reactOnAnswer(final String answer)
	{
		if (answer.equalsIgnoreCase("yes")
			|| answer.equalsIgnoreCase("y"))
		{
			this.answer = true;
			return true;
		}
		if (answer.equalsIgnoreCase("no")
			|| answer.equalsIgnoreCase("n"))
		{
			this.answer = false;
			return true;
		}
		return false;
	}

	public boolean getAnswer()
	{
		return answer;
	}

	protected void setAnswer(final boolean answer)
	{
		this.answer = answer;
	}
}
