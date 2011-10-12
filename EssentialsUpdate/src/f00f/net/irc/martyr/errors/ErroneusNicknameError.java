package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.util.FullNick;

/**
 * Code: 432 ERR_ERRONEUSNICKNAME
 * &lt;nick&gt; :Erroneus nickname
 * Returned after receiving a NICK message which contains characters which do not fall in the defined set.
 */
public class ErroneusNicknameError extends GenericError
{
    private FullNick nick;
    private String errorMessage;

    public ErroneusNicknameError()
    {
    }

    public ErroneusNicknameError(FullNick nick, String errorMessage)
    {
        this.nick = nick;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "432";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new ErroneusNicknameError(new FullNick(getParameter(params, 1)), getParameter(params, 2));
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

