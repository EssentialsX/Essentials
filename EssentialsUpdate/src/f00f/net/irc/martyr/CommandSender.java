package f00f.net.irc.martyr;

/**
 * A CommandSender can accept an OutCommand and do something with it
 * (such as send it to the server, or send it on to another
 * CommandSender).  The idea is to create a chain of CommandSenders,
 * with the last object in the chain the default CommandSender,
 * created by IRCConnection.
 * */
public interface CommandSender
{
	CommandSender getNextCommandSender();
	void sendCommand( OutCommand command );
}
