package f00f.net.irc.martyr;

import java.util.Observable;
import java.util.Observer;

/**
 * Provides a framework for an auto service that operates with
 * InCommands.  Does enable by default.  Splits the 'update' method
 * into two, 'updateState' and 'updateCommand'.  Also provides thread
 * safety on all methods.
 */
public abstract class GenericCommandAutoService implements Observer
{

protected boolean enabled = false;
protected IRCConnection connection;

protected GenericCommandAutoService( IRCConnection connection )
{
	this.connection = connection;

	enable();
}

public synchronized void enable()
{
	if( enabled )
		return;
	
	connection.addCommandObserver( this );
	enabled = true;
}

public synchronized void disable()
{
	if( !enabled )
		return;
		
	connection.removeCommandObserver( this );
	enabled = false;
}

public synchronized void update( Observable observer, Object updated )
{
	if( !enabled )
		throw new IllegalStateException("This observer is not enabled." );
	if( updated instanceof State )
	{
		throw new IllegalArgumentException("This is not a state observer." );
	}
	else if( updated instanceof InCommand )
	{
		updateCommand( (InCommand)updated );
	}
	else
	{
		throw new IllegalArgumentException("Unknown object given to update.");
	}
}

protected IRCConnection getConnection()
{
	return connection;
}

protected synchronized boolean isEnabled()
{
	return enabled;
}

protected abstract void updateCommand( InCommand command );


// END AutoRegister
}
 


 
