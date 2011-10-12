/*
 * Original version: Ben Damm <bdamm@dammfine.com>
 * Changes by: Mog
 * 	- Retains the nick that is in use
 * 	*/
package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.State;
import f00f.net.irc.martyr.util.FullNick;

/**
 * Code: 433 ERR_ERRONEUSNICKNAME
 * &lt;nick&gt; :Nickname is already in use
 * Returned when a NICK message is processed that result in an attempt to change
 * to a currently existing nickname.
 * TODO: Should we rename this to NicknameInUseError for consistency with rest of errors/matching RFC?
 */
public class NickInUseError extends GenericError
{
    private FullNick _nick;
    String errorMessage;

    public NickInUseError()
    {
        _nick = null;
    }
    public NickInUseError(FullNick nick, String errorMessage)
    {
        _nick = nick;
        this.errorMessage = errorMessage;
    }

    public State getState()
    {
        return State.UNKNOWN;
    }

    public String getIrcIdentifier()
    {
        return "433";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NickInUseError(new FullNick(getParameter(params, 1)), getParameter(params, 2));
    }

    /**
     * @return The nick in use.
     */
    public FullNick getNick()
    {
        return _nick;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

