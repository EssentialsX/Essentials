package com.earth2me.essentials.yaml;

import java.io.Serializable;


public class General implements Serializable
{
	private boolean debug = false;
	private boolean updateCheck = true;
	private String location = "null";

	public General()
	{
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(final boolean debug)
	{
		this.debug = debug;
	}

	public boolean isUpdateCheck()
	{
		return updateCheck;
	}

	public void setUpdateCheck(final boolean updateCheck)
	{
		this.updateCheck = updateCheck;
	}
	
	public boolean isStupid()
	{
		return true;
	}
	
	public void setStupid(final boolean bla)
	{
		return;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(final String location)
	{
		this.location = location;
	}
}
