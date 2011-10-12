package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 424 ERR_FILEERROR
 * :File error doing &lt;file op&gt; on &lt;file&gt;
 * Generic error message used to report a failed file operation during the processing of a message.
 */
public class FileErrorError extends GenericError
{
    private String errorMessage;

    public FileErrorError()
    {
    }

    public FileErrorError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "424";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new FileErrorError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

