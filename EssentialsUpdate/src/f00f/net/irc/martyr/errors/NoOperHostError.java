package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 491 ERR_NOOPERHOST
 * :No O-lines for your host
 * If a client sends an OPER message and the server has not been configured to allow
 * connections from the client's host as an operator, this error must be returned.
 */
public class NoOperHostError extends GenericError
{
    private String errorMessage;

    public NoOperHostError()
    {
    }

    public NoOperHostError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "491";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoOperHostError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

