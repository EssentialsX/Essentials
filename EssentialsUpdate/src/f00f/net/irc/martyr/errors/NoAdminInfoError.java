package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 423 ERR_NOADMININFO
 * &lt;server name&gt; :No administrative info available
 * Returned by a server in response to an ADMIN message when there is an error in finding the
 * appropriate information.
 */
public class NoAdminInfoError extends GenericError
{
    private String server;
    private String errorMessage;

    public NoAdminInfoError()
    {
    }

    public NoAdminInfoError(String server, String errorMessage)
    {
        this.server = server;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "423";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoAdminInfoError(getParameter(params, 1), getParameter(params, 2));
    }

    public String getServer()
    {
        return server;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

