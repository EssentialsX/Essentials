package f00f.net.irc.martyr.replies;

public abstract class GenericStringReply extends GenericReply
{

    private String string;

    public GenericStringReply()
    {
    }

    public GenericStringReply( String string )
    {
        this.string = string;
    }

    public String getString()
    {
        return string;
    }

}

