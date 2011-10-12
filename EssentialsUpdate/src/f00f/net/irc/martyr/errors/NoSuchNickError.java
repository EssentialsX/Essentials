package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.util.FullNick;

/**
 * Code: 401 ERR_NOSUCHNICK
 * &lt;nickname&gt; :No such nick/channel
 * Used to indicated the nickname parameter supplied to a command is currently unused.
 */
public class NoSuchNickError extends GenericError
{
    private FullNick nick;
    private String errorMessage;

    public NoSuchNickError()
    {
    }

    public NoSuchNickError(FullNick nick, String errorMessage)
    {
        this.nick = nick;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "401";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoSuchNickError(new FullNick(getParameter(params, 1)), getParameter(params, 2));
    }

    public FullNick getNick()
    {
        return nick;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

