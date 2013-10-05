package net.ess3.api.events;

import net.ess3.api.IUser;


public class JailStatusChangeEvent extends StatusChangeEvent
{
	public JailStatusChangeEvent(IUser affected, IUser controller, boolean value)
	{
		super(affected, controller, value);
	}
}