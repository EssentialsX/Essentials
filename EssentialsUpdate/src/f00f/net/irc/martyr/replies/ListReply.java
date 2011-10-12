package f00f.net.irc.martyr.replies;

import f00f.net.irc.martyr.InCommand;

/**
 * Signals an entry of a LIST response.
 *
 * @author Daniel Henninger
 */
public class ListReply extends GenericReply
{

    private String requestor;
    private String channel;
    private Integer memberCount;
    private String topic;

    /**
	 * Factory constructor.
	 */
	public ListReply()
    {
	}

    public ListReply(String requestor, String channel, Integer memberCount, String topic)
    {
        this.requestor = requestor;
        this.channel = channel;
        this.memberCount = memberCount;
        this.topic = topic;
    }

    public String getIrcIdentifier()
    {
		return "322";
	}

    public InCommand parse( String prefix, String identifier, String params )
    {
		return new ListReply(getParameter(params, 0), getParameter(params, 1), Integer.parseInt(getParameter(params, 2)), getParameter(params, 3));
	}

    public String getChannel()
    {
        return channel;
    }

    public Integer getMemberCount()
    {
        return memberCount;
    }

    public String getTopic()
    {
        return topic;
    }

    public String getRequestor()
    {
        return requestor;
    }
    
}
