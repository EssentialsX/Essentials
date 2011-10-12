package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 472 ERR_UNKNOWNMODE
 * &lt;char&gt; :is unknown mode char to me
 */
public class UnknownModeError extends GenericError
{
    private Character mode;
    private String errorMessage;

    public UnknownModeError()
    {
    }

    public UnknownModeError(Character mode, String errorMessage)
    {
        this.mode = mode;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "472";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new UnknownModeError(getParameter(params, 1).charAt(0), getParameter(params, 2));
    }

    public Character getMode()
    {
        return mode;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

