package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.State;


/**
 * Some unknown command, for which there is no factory.  This is a
 * special case command, created by IRCConnection if it can't find a
 * proper command object.
 */
public class UnknownCommand extends AbstractInCommand
{

    public State getState()
    {
        return State.UNKNOWN;
    }

    /**
     * Never parsed.
     */
    public InCommand parse( String prefix, String identifier, String params )
    {
        throw new UnsupportedOperationException("UnknownCommand does no parsing.");
    }

    /**
     * Unknown, so we don't know what the identifier is ahead of time.
     */
    public String getIrcIdentifier()
    {
        return null;
    }

}


