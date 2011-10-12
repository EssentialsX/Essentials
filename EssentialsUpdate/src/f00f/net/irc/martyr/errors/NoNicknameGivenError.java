package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 431 ERR_NONICKNAMEGIVEN
 * :No nickname given
 * Returned when a nickname parameter expected for a command and isn't found.
 */
public class NoNicknameGivenError extends GenericError
{
    private String errorMessage;

    public NoNicknameGivenError()
    {
    }

    public NoNicknameGivenError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "431";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoNicknameGivenError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

