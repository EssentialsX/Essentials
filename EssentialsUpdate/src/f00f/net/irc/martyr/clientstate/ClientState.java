/*
 * Original version: Ben Damm <bdamm@dammfine.com>
 * Changes by: mog
 *	- Added isOnChannel
 *
 */
package f00f.net.irc.martyr.clientstate;

import java.util.Enumeration;
import java.util.Hashtable;

import f00f.net.irc.martyr.util.FullNick;
//import org.apache.log4j.Logger;

/**
 * <p>Maintains a list of client-related facts such as what channels we
 * are in, who else is in the channels, what our nick is, etc.</p>
 *
 * <p>ClientState is a critical part of martyr.  To get access to events
 * that change the client state, the framework user can subclass
 * ClientState and then pass the subclass to the IRCConnection's
 * constructor.  Then, when a command detects a change in client
 * state, it will call the corresponding method in the custom
 * ClientState.</p>
 *
 * <p>If a user of the framework wishes to grab client state information
 * about a channel (when a user joins, when a user leaves, topic
 * change, etc), the user can do so in a similar manner.  Simply
 * override the 'addChannel(String)' method to instantiate their own
 * Channel subclass, and call the protected 'addChannel' method.  See
 * the addChannel method for an example.
 * </p>
 *
 */
public class ClientState
{

    //static Logger log = Logger.getLogger(ClientState.class);

    private FullNick nick = null;
    private String user = "";
    private String name = "";
    private String pass = null;
    private String server = "";
    private int port = -1;

    // Hashtable is threadsafe so we don't have to be.
    protected Hashtable<String,Channel> channels = new Hashtable<String,Channel>();

    public void setNick( FullNick nick )
    {
        if( nick == null )
        {
            //log.debug("ClientState: Set nick to null");
        }
        else
        {
            //log.debug("ClientState: Set nick to \"" + nick + "\"");
        }
        this.nick = nick;
    }

    public FullNick getNick()
    {
        return nick;
    }

    public void setUser( String user )
    {
        this.user = user;
    }

    /**
     * @return the username that was used to register.
     * */
    public String getUser()
    {
        return user;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * @return the name (any arbitrary string) that was used to register.
     * */
    public String getName()
    {
        return name;
    }

    /**
     * @return the password that was used to register.
     */
    public String getPass()
    {
        return pass;
    }

    public void setPass(String pass)
    {
        this.pass = pass;
    }

    public void setServer( String server )
    {
        this.server = server;
    }

    public String getServer()
    {
        return server;
    }

    public void setPort( int port )
    {
        this.port = port;
    }

    public int getPort()
    {
        return port;
    }

    /**
     * <p>Adds a channel to the list of channels we know about.  If you
     * want to supply your own Channel object, override this method
     * with:</p>
     * <pre>
     * public void addChannel( String channame )
     * {
     *     addChannel( new MyChannel( channame ) );
     * }
     * </pre>
     *
     * @param channame Channel to add to list of channels
     */
    public void addChannel( String channame )
    {
        addChannel( new Channel( channame ) );
    }

    protected void addChannel( Channel channel )
    {
        //log.debug("ClientState: Channel added: " + channel.getName());
        channels.put( channel.getName().toLowerCase(), channel );
    }

    public Channel getChannel( String chanName )
    {
        return channels.get( chanName.toLowerCase() );
    }

    /**
     * Removes a channel from the state, does nothing if the channel name
     * is invalid.
     * Should we throw an exception here?
     *
     * @param channel Channel to remove from list
     */
    public void removeChannel( String channel )
    {
        //log.debug("ClientState: Channel removed: " + channel);
        channels.remove( channel.toLowerCase() );
    }

    public boolean isOnChannel( String channel )
    {
        for (Enumeration iter = getChannelNames(); iter.hasMoreElements();)
        {
            if(channel.equalsIgnoreCase((String) iter.nextElement()))
            {
                return true;
            }
        }
        return false;
    }

    public Enumeration getChannelNames()
    {
        return channels.keys();
    }

    public Enumeration getChannels()
    {
        return channels.elements();
    }

}


