package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;

/**
 * Signals an automated AWAY message received as a response to a PRIVMSG that was sent out.
 *
 * @author Daniel Henninger
 */
public class AwayReply extends GenericReply
{

    private String nick;
    private String message;

    /**
	 * Factory constructor.
	 */
	public AwayReply()
    {
	}

    public AwayReply(String nick, String message)
    {
        this.nick = nick;
        this.message = message;
    }

    public String getIrcIdentifier()
    {
		return "301";
	}

    public InCommand parse( String prefix, String identifier, String params )
    {
		return new AwayReply(getParameter(params, 1), getParameter(params, 2));
	}

    public String getNick()
    {
        return nick;
    }

    public String getMessage()
    {
        return message;
    }

}
