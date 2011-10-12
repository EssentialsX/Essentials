package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.util.FullNick;

/**
 * Code: 441 ERR_USERNOTINCHANNEL
 * &lt;user&gt; &lt;channel&gt; :They aren't on that channel
 * Returned by the server to indicate that the target user of the command is not on the given channel.
 */
public class UserNotInChannelError extends GenericError
{
    private FullNick nick;
    private String channel;
    private String errorMessage;

    public UserNotInChannelError()
    {
    }

    public UserNotInChannelError(FullNick nick, String channel, String errorMessage)
    {
        this.nick = nick;
        this.channel = channel;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "441";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new UserNotInChannelError(new FullNick(getParameter(params, 1)), getParameter(params, 2), getParameter(params, 3));
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

