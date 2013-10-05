package net.ess3.api.events;

import net.ess3.api.IUser;


public class GodStatusChangeEvent extends StatusChangeEvent
{
	public GodStatusChangeEvent(IUser affected, IUser controller, boolean value)
	{
		super(affected, controller, value);
	}
}
