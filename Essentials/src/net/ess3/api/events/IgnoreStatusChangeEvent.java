package net.ess3.api.events;

import net.ess3.api.IUser;


public class IgnoreStatusChangeEvent extends StatusChangeEvent
{
	public IgnoreStatusChangeEvent(IUser affected, IUser controller, boolean value)
	{
		super(affected, controller, value);
	}
}
