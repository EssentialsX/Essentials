package net.ess3.api.events;

import net.ess3.api.IUser;


public class AfkStatusChangeEvent extends StatusChangeEvent
{
	public AfkStatusChangeEvent(IUser affected, IUser controller, boolean value)
	{
		super(affected, controller, value);
	}
}
