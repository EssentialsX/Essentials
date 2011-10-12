package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 445 ERR_SUMMONDISABLED
 * :SUMMON has been disabled
 * Returned as a response to the SUMMON command.  Must be returned by any server
 * which does not implement it.
 */
public class SummonDisabledError extends GenericError
{
    private String errorMessage;

    public SummonDisabledError()
    {
    }

    public SummonDisabledError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "445";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new SummonDisabledError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

