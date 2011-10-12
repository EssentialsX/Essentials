package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 463 ERR_NOPERMFORHOST
 * :Your host isn't among the privileged
 * Returned to a client which attempts to register with a server which does not been setup to allow
 * connections from the host the attempted connection is tried.
 */
public class NoPermForHostError extends GenericError
{
    private String errorMessage;

    public NoPermForHostError()
    {
    }

    public NoPermForHostError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "463";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoPermForHostError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

