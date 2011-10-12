package f00f.net.irc.martyr.replies;

import java.util.Date;
import java.util.StringTokenizer;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.clientstate.Channel;
import f00f.net.irc.martyr.clientstate.ClientState;

/**
 * ChannelCreationReply sets the creation time of the channel.  It is sent
 * automatically on a MODE discovery request.
 */
public class ChannelCreationReply extends GenericReply
{
	private String channelName;
	private Date date;

	/** For use as a factory. */
	public ChannelCreationReply()
	{
	}
	
	public ChannelCreationReply( String channelName, Date date )
	{
		this.channelName = channelName;
		this.date = date;
	}
	
	public String getIrcIdentifier()
	{
		return "329";
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
		
		// The channel.
		String chan = tokens.nextToken();
		
		// The date.
		Date date;
		try
		{
			date = new Date( Long.parseLong( tokens.nextToken() ) * 1000 );
		}
		catch( NumberFormatException nfe )
		{
			// riiiight...
			date = new Date(0);
		}
		
		return new ChannelCreationReply( chan, date );
	}
	
	/**
	 * This should, theoretically, never be called, because this command is
	 * only ever used as a factory.
	 */
	public boolean updateClientState( ClientState state )
	{
		Channel channel = state.getChannel( channelName );
		channel.setCreationDate( date );
		return true;
	}
}




