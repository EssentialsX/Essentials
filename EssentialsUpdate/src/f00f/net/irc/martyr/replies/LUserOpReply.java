package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;

public class LUserOpReply extends GenericStringReply
{

    private int numOps;

    public LUserOpReply()
    {
    }

    public LUserOpReply( int ops, String string )
    {
        super( string );
        this.numOps = ops;
    }

    public String getIrcIdentifier()
    {
        return "252";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new LUserOpReply( getIntParameter( params, 1, -1 ), getParameter( params, 2 ) );
    }

    public int getNumOps()
    {
        return numOps;
    }

}

