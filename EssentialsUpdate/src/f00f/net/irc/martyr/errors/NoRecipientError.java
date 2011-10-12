package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 411 ERR_NORECIPIENT
 * :No recipient given (&lt;command&gt;)
 */
public class NoRecipientError extends GenericError
{
    private String errorMessage;

    public NoRecipientError()
    {
    }

    public NoRecipientError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "411";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoRecipientError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

