package f00f.net.irc.martyr.replies;

import java.util.List;
import java.util.Arrays;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.clientstate.Channel;
import f00f.net.irc.martyr.clientstate.ClientState;
import java.util.logging.Logger;

public class NamesReply extends GenericReply
{
    static Logger log = Logger.getLogger(NamesReply.class.getName());

    private List<String> names;
    private String channel;

    /** For use as a factory. */
    public NamesReply()
    {
    }

    public NamesReply( String channel, List<String> names )
    {
        this.names = names;
        this.channel = channel;
    }

    public String getIrcIdentifier()
    {
        return "353";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NamesReply( getParameter( params, 2 ), Arrays.asList(getParameter( params, 3 ).split(" ")) );
    }

    /**
     * Adds the list of names to the client state.
     */
    public boolean updateClientState( ClientState state )
    {
        boolean stateChanged = false;

        // 1) Get the Channel
        Channel channelObj = state.getChannel( channel );

        if( channel == null )
        {
            log.severe("NamesReply: Channel is null");
            return false;
        }

        if( channelObj == null )
        {
            log.severe("NamesReply: No channel object for channel: " + channel);
            return false;
        }


        // 2) Parse out names
        for (String nick : names) {
            // 3) Check that the user is not already in the list
            if( !channelObj.isMemberInChannel( nick ) )
            {
                channelObj.addMember( nick, this );
                stateChanged = true;
            }
        }

        return stateChanged;
    }

    public List<String> getNames()
    {
        return names;
    }

    public String getChannel()
    {
        return channel;
    }

}




