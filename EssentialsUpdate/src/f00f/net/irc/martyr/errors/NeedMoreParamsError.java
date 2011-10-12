package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 461 ERR_NEEDMOREPARAMS
 * &lt;command&gt; :Not enough parameters
 * Returned by the server by numerous commands to indicate to the client that it didn't
 * supply enough parameters.
 */
public class NeedMoreParamsError extends GenericError
{
    private String command;
    private String errorMessage;

    public NeedMoreParamsError()
    {
    }

    public NeedMoreParamsError(String command, String errorMessage)
    {
        this.command = command;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "461";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NeedMoreParamsError(getParameter(params, 1), getParameter(params, 2));
    }

    public String getCommand()
    {
        return command;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

