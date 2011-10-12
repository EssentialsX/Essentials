package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.State;
import f00f.net.irc.martyr.commands.UnknownCommand;


/**
 * A container for unknown replies.
 */
public class UnknownReply extends UnknownCommand
{
	private String replyStr;
	private int replyCode;

	public UnknownReply( String ident )
	{
		replyStr = ident;
		replyCode = Integer.parseInt( ident );
	}

	public int getReplyCode()
	{
		return replyCode;
	}

	public String getReply()
	{
		return replyStr;
	}

	public static boolean isReply( String ident )
	{
		char c = ident.charAt(0);
		return ( c == '0' || c == '2' || c == '3' );
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
		throw new UnsupportedOperationException("UnknownReply does no parsing.");
	}

	/**
	 * Unknown, so we don't know what the identifier is ahead of time.
	 */
	public String getIrcIdentifier()
	{
		return replyStr;
	}

	public String toString()
	{
		return "UnknownReply[" + replyStr + "]";
	}

}


