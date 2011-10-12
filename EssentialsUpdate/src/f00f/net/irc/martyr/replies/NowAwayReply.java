package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;

/**
 * Signals that you were successfully marked un-away.
 *
 * @author Daniel Henninger
 */
public class NowAwayReply extends GenericReply
{

    /* Should always be You have been marked as being away */
    private String message;

    /**
	 * Factory constructor.
	 */
	public NowAwayReply()
    {
	}

    public NowAwayReply(String message)
    {
        this.message = message;
    }

    public String getIrcIdentifier()
    {
		return "306";
	}

    public InCommand parse( String prefix, String identifier, String params )
    {
		return new NowAwayReply(getParameter(params, 0));
	}

    public String getMessage()
    {
        return message;
    }

}
