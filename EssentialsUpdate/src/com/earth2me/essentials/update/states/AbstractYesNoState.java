package com.earth2me.essentials.update.states;


public abstract class AbstractYesNoState extends AbstractState
{
	private boolean answer = false;
	private final transient Class<? extends AbstractState> yesState;
	private final transient Class<? extends AbstractState> noState;

	public AbstractYesNoState(final StateMap states, final Class<? extends AbstractState> nextState)
	{
		this(states, nextState, nextState);
	}

	public AbstractYesNoState(final StateMap states, final Class<? extends AbstractState> yesState, final Class<? extends AbstractState> noState)
	{
		super(states);
		this.yesState = yesState;
		this.noState = noState;
	}

	@Override
	public AbstractState getNextState()
	{
		return answer
			   ? (yesState == null ? null : getState(yesState))
			   : (noState == null ? null : getState(noState));
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
