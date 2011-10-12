package f00f.net.irc.martyr;

import java.util.Observable;

/**
 * Provides a framework for an auto service.  Does enable by default.
 * Splits the 'update' method into two, 'updateState' and 'updateCommand'.
 * Also provides thread safety on all methods.
 */
public abstract class GenericAutoService extends GenericCommandAutoService
{

protected GenericAutoService( IRCConnection connection )
{
	super( connection );
}

public synchronized void enable()
{
	if( enabled )
		return;

	connection.addStateObserver( this );

	super.enable();
}

public synchronized void disable()
{
	if( !enabled )
		return;
		
	connection.removeStateObserver( this );

	super.disable();
}

public synchronized void update( Observable observer, Object updated )
{
	if( !enabled )
		throw new IllegalStateException("This observer is not enabled." );
	if( updated instanceof State )
		updateState( (State)updated );
	else 
		super.update( observer, updated );
}

protected abstract void updateState( State state );

}
 


 
