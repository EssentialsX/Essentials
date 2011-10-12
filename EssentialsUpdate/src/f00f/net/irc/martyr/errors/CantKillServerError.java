package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 483 ERR_CANTKILLSERVER
 * :You can't kill a server!
 * Any attempts to use the KILL command on a server are to be refused and this
 * error returned directly to the client.
 */
public class CantKillServerError extends GenericError
{
    private String errorMessage;

    public CantKillServerError()
    {
    }

    public CantKillServerError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "483";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new CantKillServerError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

