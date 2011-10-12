package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;

public class LUserClientReply extends GenericStringReply
{

    public LUserClientReply()
    {
    }

    public LUserClientReply( String string )
    {
        super( string );
    }

    public String getIrcIdentifier()
    {
        return "251";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new LUserClientReply( getParameter( params, 1 ) );
    }

}

