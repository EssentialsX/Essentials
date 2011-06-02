package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserManager implements IConf
{
	private final transient EssentialsConf users;
	private final transient List<String> spyusers = new ArrayList<String>();
	private final static String ADDRESS = "address";
	private final static String SPY = "spy";

	public UserManager(final File folder)
	{
		users = new EssentialsConf(new File(folder, "users.yml"));
		reloadConfig();
	}

	public final boolean isSpy(final String username)
	{
		return users.getBoolean(username.toLowerCase() + "." + SPY, false);
	}

	public void setSpy(final String username, boolean spy) throws Exception
	{
		setUser(username.toLowerCase(), getAddress(username), spy);
	}

	public final String getAddress(final String username)
	{
		return users.getString(username.toLowerCase() + "." + ADDRESS, null);
	}

	public void setAddress(final String username, final String address) throws Exception
	{
		setUser(username.toLowerCase(), address, isSpy(username));
	}

	public List<String> getSpyUsers()
	{
		return spyusers;
	}

	private void setUser(String username, String address, boolean spy) throws Exception
	{
		final Map<String, Object> userdata = new HashMap<String, Object>();
		userdata.put(ADDRESS, address);
		userdata.put(SPY, spy);
		users.setProperty(username, userdata);
		users.save();
		reloadConfig();
	}

	@Override
	public final void reloadConfig()
	{
		users.load();
		spyusers.clear();
		final List<String> keys = users.getKeys(null);
		for (String key : keys)
		{
			if (isSpy(key))
			{
				final String address = getAddress(key);
				if (address != null)
				{
					spyusers.add(address);
				}
			}
		}
	}
}
