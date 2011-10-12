package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.util.ParameterIterator;

public class WhoisUserReply extends AbstractWhoisReply
{

	private String host;
	private String name;

	/**
	 * Factory constructor.
	 * */
	public WhoisUserReply()
	{
	}

	public WhoisUserReply( String params )
	{
		super( params );
	}

	public String getIrcIdentifier()
	{
		return "311";
	}

	protected void parseParams( ParameterIterator pi )
	{
		pi.next(); // throw away the nick
		host = (String)pi.next(); // hostmask
		//log.debug("WhoisUserReply: host: " + host);
		name = pi.last(); // the "Name"
		//log.debug("WhoisUserReply: name: " + name);
	}

	public InCommand parse( String prefix, String identifier, String params )
	{
		return new WhoisUserReply( params );
	}

    public String getHost()
    {
        return host;
    }

    public String getName()
    {
        return name;
    }

}

