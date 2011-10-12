package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.IRCConnection;
import f00f.net.irc.martyr.OutCommand;
/**
 * Defines USER command, part of the handshake to register on the
 * network.
 */
public class UserCommand implements OutCommand
{

    private String name;
    private String user;
    private String someA; // Might be a mode on some networks
    private String someB; // might be ignored

    public static final String IDENTIFIER = "USER";

    /**
     * @param user the login name on the computer the client is on
     * @param name the purported full name of the user, can be anything.
     * @param connection the connection the user command is affiliated with
     * */
    public UserCommand( String user, String name, IRCConnection connection )
    {
        this.name = name;
        this.user = user;
        //localhost = connection.getLocalhost();
        //remotehost = connection.getRemotehost();
        someA = "0"; // Can be 0|4|8, with 4=+w, 8=+i
        someB = connection.getRemotehost(); // ignored, apparently
    }

    public String render()
    {
        return IDENTIFIER + " " + user + " " + someA + " " + someB + " :" + name;
    }

    public String getIrcIdentifier()
    {
        return IDENTIFIER;
    }

}


