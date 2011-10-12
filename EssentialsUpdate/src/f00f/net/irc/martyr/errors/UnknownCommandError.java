package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 421 ERR_UNKNOWNCOMMAND
 * &lt;command&gt; :Unknown command
 * Returned to a registered client to indicate that the command sent is unknown by the server.
 */
public class UnknownCommandError extends GenericError
{
    private String command;
    private String errorMessage;

    public UnknownCommandError()
    {
    }

    public UnknownCommandError(String command, String errorMessage)
    {
        this.command = command;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "421";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new UnknownCommandError(getParameter(params, 1), getParameter(params, 2));
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

