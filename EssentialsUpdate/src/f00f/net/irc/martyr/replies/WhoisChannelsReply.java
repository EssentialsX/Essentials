package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.util.ParameterIterator;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

//import org.apache.log4j.Logger;

public class WhoisChannelsReply extends AbstractWhoisReply
{
    //static Logger log = Logger.getLogger(WhoisChannelsReply.class);

    private String channels;

	/**
	 * Factory constructor.
	 * */
	public WhoisChannelsReply()
	{
	}

	public WhoisChannelsReply( String params )
	{
		super( params );
	}

	public String getIrcIdentifier()
	{
		return "319";
	}

	/**
	 * @return a space-delimited list of channels
	 * */
	public String getChannels()
	{
		return channels;
	}

	/**
	 * @return a set of Strings of channels
	 * */
	public Set<String> getChannelSet()
	{
		StringTokenizer tokens = new StringTokenizer( channels );
		Set<String> set = new HashSet<String>();
		while( tokens.hasMoreTokens() )
		{
			set.add( tokens.nextToken() );
		}

		return set;
	}

	protected void parseParams( ParameterIterator pi )
	{
		channels = pi.last(); // Channels

		//log.debug("WhoisChannelsReply: channels: " + channels);
	}

	public InCommand parse( String prefix, String identifier, String params )
	{
		return new WhoisChannelsReply( params );
	}

}

