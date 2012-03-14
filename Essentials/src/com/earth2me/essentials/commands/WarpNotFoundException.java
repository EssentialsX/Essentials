package com.earth2me.essentials.commands;


public class WarpNotFoundException extends Exception
{
	public WarpNotFoundException()
	{
		super("Warp not found");
	}
	
	public WarpNotFoundException(String message)
	{
		super(message);
	}
}
