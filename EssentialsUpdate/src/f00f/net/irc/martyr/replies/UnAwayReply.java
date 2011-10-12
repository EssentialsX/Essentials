package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;

/**
 * Signals that you were successfully marked un-away.
 *
 * @author Daniel Henninger
 */
public class UnAwayReply extends GenericReply
{

    /* Should always be You are no longer marked as being away */
    private String message;

    /**
	 * Factory constructor.
	 */
	public UnAwayReply()
    {
	}

    public UnAwayReply(String message)
    {
        this.message = message;
    }

    public String getIrcIdentifier()
    {
		return "305";
	}

    public InCommand parse( String prefix, String identifier, String params )
    {
		return new UnAwayReply(getParameter(params, 0));
	}

    public String getMessage()
    {
        return message;
    }

}
