package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;

/**
 * Signals the end of a LIST response.
 *
 * @author Daniel Henninger
 */
public class ListEndReply extends GenericReply
{

    /**
	 * Factory constructor.
	 */
	public ListEndReply()
    {
	}

    public String getIrcIdentifier()
    {
		return "323";
	}

    public InCommand parse( String prefix, String identifier, String params )
    {
		return new ListEndReply();
	}

}

