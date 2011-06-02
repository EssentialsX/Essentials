package com.earth2me.essentials.xmpp;

import java.util.List;
import org.bukkit.entity.Player;


public interface IEssentialsXMPP
{

	String getAddress(final Player user);

	String getAddress(final String name);

	List<String> getSpyUsers();

	void sendMessage(final Player user, final String message);

	void sendMessage(final String address, final String message);

	void setAddress(final Player user, final String address) throws Exception;

	boolean toggleSpy(final Player user) throws Exception;
	
}
