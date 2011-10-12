package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.util.FullNick;

/**
 * Code: 443 ERR_USERONCHANNEL
 * &lt;user&gt; &lt;channel&gt; :is already on channel
 * Returned when a client tries to invite a user to a channel they are already on.
 */
public class UserOnChannelError extends GenericError
{
    private FullNick nick;
    private String channel;
    private String errorMessage;

    public UserOnChannelError()
    {
    }

    public UserOnChannelError(FullNick nick, String channel, String errorMessage)
    {
        this.nick = nick;
        this.channel = channel;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "443";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new UserOnChannelError(new FullNick(getParameter(params, 1)), getParameter(params, 2), getParameter(params, 3));
    }

    public FullNick getNick()
    {
        return nick;
    }

    public String getChannel()
    {
        return channel;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

