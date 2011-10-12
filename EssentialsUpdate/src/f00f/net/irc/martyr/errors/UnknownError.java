package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.State;
import f00f.net.irc.martyr.commands.UnknownCommand;


/**
 * Some unknown command, for which there is no factory.  This is a
 * special case command, created by IRCConnection if it can't find a
 * proper command object.
 */
public class UnknownError extends UnknownCommand
{

	private String errorStr;
	private int errorCode;

	public UnknownError( String ident )
	{
		errorStr = ident;
		errorCode = Integer.parseInt( ident );
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	public String getError()
	{
		return errorStr;
	}

	public static boolean isError( String ident )
	{
		char c = ident.charAt(0);
		return ( c == '4' || c == '5' );
	}

	public State getState()
	{
		return State.UNKNOWN;
	}

	/** 
	 * Never parsed.
	 */
	public InCommand parse( String prefix, String identifier, String params )
	{
		throw new UnsupportedOperationException("UnknownError does no parsing.");
	}

	/**
	 * Unknown, so we don't know what the identifier is ahead of time.
	 */
	public String getIrcIdentifier()
	{
		return errorStr;
	}

	public String toString()
	{
		return "UnknownError[" + errorStr + "]";
	}

}


