package com.earth2me.essentials.chat;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;


public class ChatStore
{
	private final User user;
	private final String type;
	private final Trade charge;
	private long radius;

	ChatStore(final IEssentials ess, final User user, final String type)
	{
		this.user = user;
		this.type = type;
		this.charge = new Trade(getLongType(), ess);
	}

	public User getUser()
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
		return type.length() == 0 ? "chat" : "chat-" + type;
	}

	public long getRadius()
	{
		return radius;
	}

	public void setRadius(long radius)
	{
		this.radius = radius;
	}
}
