package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.util.FullNick;

/**
 * Code: 436 ERR_NICKCOLLISION
 * &lt;nick&gt; :Nickname collision KILL
 * Returned by a server to a client when it detects a nickname collision (registered of a NICK that
 * already exists by another server).
 */
public class NickCollisionError extends GenericError
{
    private FullNick nick;
    private String errorMessage;

    public NickCollisionError()
    {
    }

    public NickCollisionError(FullNick nick, String errorMessage)
    {
        this.nick = nick;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "436";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NickCollisionError(new FullNick(getParameter(params, 1)), getParameter(params, 2));
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

