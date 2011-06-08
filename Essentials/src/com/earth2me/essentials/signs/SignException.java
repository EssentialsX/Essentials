package com.earth2me.essentials.signs;


public class SignException extends Exception
{
	public SignException(final String message)
	{
		super(message);
	}

	public SignException(final String message, final Throwable throwable)
	{
		super(message, throwable);
	}
}
