package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.util.FullNick;

/**
 * Code: 444 ERR_NOLOGIN
 * &lt;user&gt; :User not logged in
 * Returned by the summon after a SUMMON command for a user was unable to be performed
 * since they were not logged in.
 */
public class NoLoginError extends GenericError
{
    private FullNick nick;
    private String errorMessage;

    public NoLoginError()
    {
    }

    public NoLoginError(FullNick nick, String errorMessage)
    {
        this.nick = nick;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "444";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoLoginError(new FullNick(getParameter(params, 1)), getParameter(params, 2));
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

