package com.earth2me.essentials.xmpp;

import java.util.List;
import org.bukkit.entity.Player;


public interface IEssentialsXMPP
{

	String getAddress(final Player user);

	String getAddress(final String name);

	List<String> getSpyUsers();
	
	String getUserByAddress(final String address);

	boolean sendMessage(final Player user, final String message);

	boolean sendMessage(final String address, final String message);

	void setAddress(final Player user, final String address);

	boolean toggleSpy(final Player user);
	
}
