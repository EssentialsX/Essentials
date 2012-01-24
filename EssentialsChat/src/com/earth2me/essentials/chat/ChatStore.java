package com.earth2me.essentials.chat;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;


public class ChatStore
{
	private final transient IUser user;
	private final transient String type;
	private final transient Trade charge;
	private long radius;

	public ChatStore(final IEssentials ess, final IUser user, final String type)
	{
		this.user = user;
		this.type = type;
		this.charge = new Trade(getLongType(), ess);
	}

	public IUser getUser()
	{
		return user;
	}

	public Trade getCharge()
	{
		return charge;
	}

	public String getType()
	{
		return type;
	}

	public final String getLongType()
	{
		return type.length() > 0 ? "chat" : "chat-" + type;
	}

	public long getRadius()
	{
		return radius;
	}

	public void setRadius(final long radius)
	{
		this.radius = radius;
	}
}
