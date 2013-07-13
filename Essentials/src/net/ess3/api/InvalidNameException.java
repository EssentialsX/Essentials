package net.ess3.api;


public class InvalidNameException extends Exception
{
	/**
	 * NOTE: This is not implemented yet, just here for future 3.x api support
	 * Allow serialization of the InvalidNameException exception
	 */
	private static final long serialVersionUID = 1485321420293663139L;

	public InvalidNameException(Throwable thrwbl)
	{
		super(thrwbl);
	}
}
