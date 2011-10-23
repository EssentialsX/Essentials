package com.earth2me.essentials.update.states;

import java.util.LinkedHashMap;


public class StateMap extends LinkedHashMap<Class<? extends AbstractState>, AbstractState>
{
	public StateMap()
	{
		super();
	}
	
	public AbstractState add(AbstractState state)
	{
		return put(state.getClass(), state);
	}
}
