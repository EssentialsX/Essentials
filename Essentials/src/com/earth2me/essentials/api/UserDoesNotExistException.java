package com.earth2me.essentials.api;

import com.earth2me.essentials.Util;

public class UserDoesNotExistException extends Exception
{

	public UserDoesNotExistException(String name)
	{
		super(Util.format("userDoesNotExist", name));
	}
	
}
