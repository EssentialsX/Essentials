package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;

public class NamesEndReply extends GenericReply
{

    private String channel;
    private String comment;

    /** For use as a factory. */
    public NamesEndReply()
    {
        this( null, null );
    }

    public NamesEndReply( String channel, String comment )
    {
        this.channel = channel;
        this.comment = comment;
    }

    public String getIrcIdentifier()
    {
        return "366";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NamesEndReply( getParameter( params, 1 ), getParameter( params, 2 ) );
    }

    public String getChannel()
    {
        return channel;
    }

    public String getComment()
    {
        return comment;
    }

}




