package f00f.net.irc.martyr.replies;

import java.util.StringTokenizer;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.clientstate.ClientState;
import f00f.net.irc.martyr.commands.ChannelModeCommand;

/**
 * ModeReply is really a factory that passes the ModeReply off to a
 * ChannelModeCommand.
 */
public class ModeReply extends GenericReply
{

	/** For use as a factory. */
	public ModeReply()
	{
	}
	
	public String getIrcIdentifier()
	{
		return "324";
	}
	
	/**
	 * This is a factory that passes the command off to a
	 * ChannelModeCommand.
	 */
	public InCommand parse( String prefix, String identifier, String params )
	{
		StringTokenizer tokens = new StringTokenizer( params );
	
		// Our nick.  We don't need that, I think.
		tokens.nextToken();
		
		String chan = tokens.nextToken();
		
		return new ChannelModeCommand( prefix, chan, tokens );
	}
	
	/**
	 * This should, theoretically, never be called, because this command is
	 * only ever used as a factory.
	 */
	public boolean updateClientState( ClientState state )
	{
		throw new IllegalStateException("This shouldn't be called!" );
	}
}




