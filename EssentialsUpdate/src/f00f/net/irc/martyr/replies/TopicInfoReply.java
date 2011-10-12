package f00f.net.irc.martyr.replies;

import java.util.Date;
import java.util.StringTokenizer;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.clientstate.Channel;
import f00f.net.irc.martyr.clientstate.ClientState;

/**
 * Contains info about the topic, who set it and when.
 */
public class TopicInfoReply extends GenericReply
{
	private String channelName;
	private Date date;
	private String author;

	/** For use as a factory. */
	public TopicInfoReply()
	{
	}
	
	public TopicInfoReply( String channelName, Date date, String author )
	{
		this.channelName = channelName;
		this.date = date;
		this.author = author;
	}
	
	public String getIrcIdentifier()
	{
		return "333";
	}

    public String getChannel()
    {
        return this.channelName;
    }
	
    public InCommand parse( String prefix, String identifier, String params )
	{
		StringTokenizer tokens = new StringTokenizer( params );
	
		// Our nick.  We don't need that, I think.
		tokens.nextToken();
		
		// The channel.
		String chan = tokens.nextToken();
		
		// The author
		String author = tokens.nextToken();
		
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
		
		return new TopicInfoReply( chan, date, author );
	}
	
	public boolean updateClientState( ClientState state )
	{
		Channel channel = state.getChannel( channelName );
		channel.setTopicDate( date );
		channel.setTopicAuthor( author );
		return true;
	}
}




