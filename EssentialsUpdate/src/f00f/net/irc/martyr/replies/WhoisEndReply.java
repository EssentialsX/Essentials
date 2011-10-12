package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.util.ParameterIterator;

public class WhoisEndReply extends AbstractWhoisReply
{
	/**
	 * Factory constructor.
	 * */
	public WhoisEndReply()
	{
	}

	public WhoisEndReply( String params )
	{
		super( params );
	}

	public String getIrcIdentifier()
	{
		return "318";
	}


	protected void parseParams( ParameterIterator pi )
	{
		// nothing to do here
	}

	public InCommand parse( String prefix, String identifier, String params )
	{
		return new WhoisEndReply( params );
	}

}

