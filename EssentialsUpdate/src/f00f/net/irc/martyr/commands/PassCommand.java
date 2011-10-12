package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.OutCommand;

/**
 * Defines PASS command, optional part of the handshake to register on the network.
 * @author Daniel Henninger
 */
public class PassCommand implements OutCommand
{
    private String pass;

    public static final String IDENTIFIER = "PASS";

    /**
     * @param pass the password for the user who is authenticating
     * */
    public PassCommand(String pass)
    {
        this.pass = pass;
    }

    public String render()
    {
        return IDENTIFIER + " " + pass;
    }

    public String getIrcIdentifier()
    {
        return IDENTIFIER;
    }

}
