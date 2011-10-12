package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 402 ERR_NOSUCHSERVER
 * &lt;server name&gt; :No such server
 * Used to indicate the server name given currently doesn't exist.
 */
public class NoSuchServerError extends GenericError
{
    private String server;
    private String errorMessage;

    public NoSuchServerError()
    {
    }

    public NoSuchServerError(String server, String errorMessage)
    {
        this.server = server;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "402";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoSuchServerError(getParameter(params, 1), getParameter(params, 2));
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

