package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.util.FullNick;

/**
 * Code: 406 ERR_WASNOSUCHNICK
 * &lt;nickname&gt; :There was no such nickname
 * Returned by WHOWAS to indicate there is no history information for that nickname.
 */
public class WasNoSuchNickError extends GenericError
{
    private FullNick nick;
    private String errorMessage;

    public WasNoSuchNickError()
    {
    }

    public WasNoSuchNickError(FullNick nick, String errorMessage)
    {
        this.nick = nick;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "406";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new WasNoSuchNickError(new FullNick(getParameter(params, 1)), getParameter(params, 2));
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

