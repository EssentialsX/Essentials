package f00f.net.irc.martyr;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ClientStateMonitor asks commands to update the client state.
 */
public class ClientStateMonitor implements Observer
{

    static Logger log = Logger.getLogger(ClientStateMonitor.class.getName());

    private IRCConnection connection;

    private boolean enabled = false;

    /**
     * This should only be called by the IRCConnection itself.
     *
     * @param connection Connection we are associated with
     */
    ClientStateMonitor( IRCConnection connection )
    {
        this.connection = connection;

        enable();
    }

    public void enable()
    {
        if( enabled )
            return;
        enabled = true;

        connection.addCommandObserver( this );
    }

    public void disable()
    {
        if( !enabled )
            return;
        connection.removeCommandObserver( this );
        enabled = false;
    }

    public void update( Observable observable, Object command_o )
    {
        InCommand command = (InCommand)command_o;

        try
        {
            /*if( */command.updateClientState( connection.getClientState() );// )
                //log.debug("ClientStateMonnitor: Client state updated");
        }
        catch( Throwable e )
        {
            log.log(Level.SEVERE,"ClientStateMonitor: Client state update failed.", e);
        }

    }

    // ===== END ClientStateMonitor
}

