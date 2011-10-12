package f00f.net.irc.martyr.services;

import java.util.Iterator;
import java.util.NoSuchElementException;

import f00f.net.irc.martyr.IRCConnection;
import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.GenericAutoService;
import f00f.net.irc.martyr.State;
import f00f.net.irc.martyr.TimerTaskCommand;
import f00f.net.irc.martyr.clientstate.ClientState;
import f00f.net.irc.martyr.commands.NickCommand;
import f00f.net.irc.martyr.commands.UserCommand;
import f00f.net.irc.martyr.commands.PassCommand;
import f00f.net.irc.martyr.errors.NickInUseError;
import f00f.net.irc.martyr.util.FullNick;
import java.util.logging.Logger;

/**
 * <p>AutoRegister performs the task of registering the user with the server
 * once connected, including finding an appropriate nickname to use if the
 * desired one is taken.</p>
 *
 * <p>AutoRegister's default behaviour is to send the provided nickname.  If it
 * receives an ERR_NICKNAMEINUSE while unregistered, AutoRegister will try
 * again, with an _ appended to the nick.  If this fails five times,
 * AutoRegister will ask the IRCConnection to disconnect().  Note that if it
 * fails to connect it remains enabled, so that if IRCConnection.connect() is
 * called, it will re-try the same 5 NICKs.</p>
 *
 * <p>This default behaviour can be overridden by subclassing AutoRegister and
 * overriding the getNickIterator( String baseNick ) method.  It returns an
 * instance of the java.util.Iterator interface which supplies nicknames (each
 * object retreived from the Iterator is presumed to be a String).
 * AutoRegister will iterate through the nickname list until there are no more
 * items, at which point it will stop.  For simple tasks such as providing a
 * custom way to form new nicknames, overriding getNickIterator is
 * sufficient.</p>
 *
 * <p>AutoRegister will add itself as a state observer and as a command
 * observer.  It needs to receive the error.NickInUseError command so that
 * it can re-try the registration, and it needs to detect when we
 * transition into the UNREGISTERED state.</p>
 *
 * <p>AutoRegister should be created before the IRCConnection.connect()
 * is called.  AutoRegister can be disabled by calling the 'disable()'
 * method at any time.  This simply removes AutoRegister as an
 * observer for the state and commands.</p>
 *
 */
public class AutoRegister extends GenericAutoService
{
    static Logger log = Logger.getLogger(AutoRegister.class.getName());

    // I've lost track of why the timer stuff was in here.  I think the
    // original purpose was to make AutoRegister take control of the nick
    // more *after* registration occurred.  This code is now causing so
    // many problems *before* registration, that I think it may need to be
    // pulled out.  Maybe time to bring a "Manager" service into the
    // fold?
    private long nickTimerTaskDelay = 10*1000;
    private TimerTaskCommand nickTimerTask;

    // Kept so it can be passed to getNickIterator()
    private String originalNick;
    // Used to set the client state once we register properly.
    private String lastTryNick = null;
    // Passed to the server on login
    private String user;
    private String name;
    private String pass;
    // Our list of nicks.
    private Iterator nickIterator = null;
    // attempt is only used for the debug output.
    private int attempt = 0;

    public static final int MAX_ATTEMPTS = 5;

    public AutoRegister( IRCConnection connection, String nick,
        String user, String name )
    {
        super( connection );

        this.originalNick = nick;
        this.user = user;
        this.name = name;
        this.pass = null;

        enable();
    }

    public AutoRegister( IRCConnection connection, String nick,
        String user, String name, String pass)
    {
        super( connection );

        this.originalNick = nick;
        this.user = user;
        this.name = name;
        this.pass = pass;

        enable();
    }


    /**
     * <p>This method supplies an Iterator that generates nicknames.  Each successive
     * failed attempt to login to the server with a nickname will be met with a new
     * try using the next nickname in the iterator.  When there are no more
     * nicknames in the Iterator, AutoRegister gives up.  Defining the Iterator as
     * an anonymous class works well.</p>
     *
     * <p>The iterator should iterate over String objects.</p>
     *
     * @param baseNick The nickname passed into the constructor.
     * @return Iterator over other attempts of nicks to try
     */
    protected Iterator getNickIterator( final String baseNick )
    {
        // This is simple and clean.. define the nick generation scheme as an
        // anonymous class.
        return new UnderscoreIterator(baseNick);
    }

    private static class UnderscoreIterator implements Iterator
    {
        int count = 1;
        String nick;

        public UnderscoreIterator( String base )
        {
            this.nick = base;
        }

        public boolean hasNext()
        {
            return count <= MAX_ATTEMPTS;
        }

        public Object next()
        {
            if( hasNext() )
            {
                String result = nick;

                // Set up the next round
                nick = nick + "_";
                ++count;

                // return the value for this round.
                return result;
            }
            else
            {
                throw new NoSuchElementException("No more nicknames");
            }
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    protected void updateState( State state )
    {
        //log.debug("AutoRegister: Update with state " + state);
        if( state == State.UNREGISTERED )
        {
            // We need to do some registerin'!
            nickIterator = getNickIterator( originalNick );
            attempt = 0;
            doRegister();
        }
        else if( state == State.REGISTERED )
        {
            // We need to update the client state.
            ClientState clientState = getConnection().getClientState();
            clientState.setNick( new FullNick( lastTryNick ) );
            clientState.setName( name );
            clientState.setUser( user );
            clientState.setPass( pass );
        }

        //log.debug("AutoRegister: Returned from " + state);
    }

    protected void updateCommand( InCommand command )
    {
        // First, check the state, because if we are already registered
        // then none of this matters.
        //if( getConnection().getState() == State.REGISTERED )
        //{
        //	// We're registered.
        //	// No reason to continue.
        //	return;
        //}

        if( command instanceof NickInUseError)
        {
            // If we get an error, then try another nick
            NickInUseError nickErr = (NickInUseError)command;
            if(nickErr.getNick().getNick().equals(originalNick))
            {
                cancelNickAttempt(); // We don't want more than one of these

                scheduleNickAttempt();
            }
            if(getConnection().getState() == State.UNREGISTERED )
            {
                // re-register.
                doRegister();
            }
        }
        else if( command instanceof NickCommand )
        {
            // If we get a nick... then cancel a pending change
            NickCommand nickCmd = (NickCommand)command;
            if( nickCmd.getOldNick().equalsIgnoreCase( originalNick ) )
            {
                cancelNickAttempt();
            }
        }
    }

    /**
     *
     */
    private void scheduleNickAttempt()
    {
        if( getConnection().getState().equals(State.REGISTERED))
        {
            // We're already connected.
            // We're short-circuiting
            return;
        }
        if(nickTimerTask == null || !nickTimerTask.isScheduled())
        {
            nickTimerTask = new TimerTaskCommand(getConnection(), new NickCommand(originalNick));
            //TODO back off delay on repeated retries?
            getConnection().getCronManager().schedule(nickTimerTask, nickTimerTaskDelay);
        }
    }

    private void cancelNickAttempt()
    {
        if(nickTimerTask != null && nickTimerTask.isScheduled())
        {
            nickTimerTask.cancel();
        }
    }

    private void doRegister()
    {
        if( getConnection().getState() != State.UNREGISTERED )
        {
            log.severe("AutoRegister: Tried to register but we are not UNREGISTERED");
            return;
        }

        if( ! nickIterator.hasNext() )
        {
            log.info("AutoRegister: Failed to register.");
            getConnection().disconnect();
            return;
        }

        lastTryNick = (String)nickIterator.next();
        ++attempt;
        log.info("AutoRegister: Trying to register as " + lastTryNick);

        if (pass != null) {
            getConnection().sendCommand( new PassCommand( pass ));
        }
        getConnection().sendCommand( new NickCommand( lastTryNick ) );
        getConnection().sendCommand( new UserCommand( user, name, getConnection() ) );
    }

    public String toString()
    {
        return "AutoRegister [" + attempt + "]";
    }

    // END AutoRegister
}
 


 
