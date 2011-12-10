package com.earth2me.essentials.user;


public class CooldownException extends Exception
{

	public CooldownException(String timeLeft)
	{
		super(timeLeft);
	}
	
}
