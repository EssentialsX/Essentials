/*
 * IRCConnection.java
 *
 *   Copyright (C) 2000, 2001, 2002, 2003, 2004 Ben Damm
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   See: http://www.fsf.org/copyleft/lesser.txt
 */

package f00f.net.irc.martyr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Observer;
import java.util.StringTokenizer;

import f00f.net.irc.martyr.clientstate.ClientState;
import f00f.net.irc.martyr.commands.UnknownCommand;
import f00f.net.irc.martyr.errors.UnknownError;
import f00f.net.irc.martyr.replies.UnknownReply;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO:
// 
// Add synchronous disconnect.
// 
/**
 * <p><code>IRCConnection</code> is the core class for Martyr.
 * <code>IRCConnection</code> manages the socket, giving commands to the server
 * and passing results to the parse engine.  It manages passing information out
 * to the application via the command listeners and state listeners.
 * <code>IRCConnection</code> has no IRC intelligence of its own, that is left
 * up to the classes on the command and state listener lists.  A number of
 * listeners that do various tasks are provided as part of the framework.</p>
 *
 * <p><b>Please read this entirely before using the framework.</b>  Or
 * what the heck, try out the example below and see if it works for ya.</p>
 * 
 * <h2>States and State Listeners</h2>
 * <p><code>IRCConnection</code> is always in one of three states.
 * <code>UNCONNECTED</code>, <code>UNREGISTERED</code> or
 * <code>REGISTERED</code>.  It keeps a list of listeners that
 * would like to know when a state change occurs.  When a state change
 * occurs each listener in the list, in the order they were added, is
 * notified.  If a listener early up on the list causes something to happen
 * that changes the state before your listener gets notified, you will be
 * notified of the state change even though the state has changed.  You
 * will be notified again of the new state.  That is, state change
 * notifications will always be in order, but they may not always reflect
 * the "current" state.</p>
 *
 * <h2>Commands and Command Listeners</h2>
 * <p><code>IRCConnection</code> also keeps a list of listeners for
 * when a command arrives from the server.  When a command arrives, it
 * is first parsed into an object.  That object is then passed around
 * to all the listeners, again, in order.  Commands can be received
 * and the socket closed before the commands are actually send to the
 * listeners, so beware that even though you receive a command, you
 * may not always be guaranteed to have an open socket to send a
 * response back on.  A consumer of the command should never modify
 * the command object.  If you try to send a command to a closed
 * socket, <code>IRCConnection</code> will silently ignore your
 * command.  Commands should always be received in order by all
 * listeners, even if a listener higher up in the list sends a
 * response to the server which elicits a response back from the
 * server before you've been told of the first command.</p>
 *
 * <h2>Connecting and staying connected</h2>
 * <p>The AutoReconnect class can connect you and will try to stay
 * connected.  Using AutoReconnect to connect the
 * first time is recommended, use the <code>go(server,port)</code> method once
 * you are ready to start.</p>
 *
 * <h2>Registration On The Network</h2>
 * <p>The AutoRegister class can register you automatically on the
 * network.  Otherwise, registration is left up to the consumer.
 * Registration should occur any time the state changes to
 * <code>UNREGISTERED</code>.  The consumer will know this because it
 * has registered some class as a state observer.
 * </p>
 *
 * <h2>Auto Response</h2>
 * <p>Some commands, such as Ping require an automatic response.
 * Commands that fall into this category can be handled by the
 * <code>AutoResponder</code> class.  For a list of what commands
 * <code>AutoResponder</code> auto responds to, see the source.</p>
 * 
 * <h2>Joining and Staying Joined</h2>
 * <p>You can use the <code>AutoJoin</code> class to join a channel
 * and stay there.  <code>AutoJoin</code> will try to re-join if
 * kicked or if the connection is lost and the server re-connects.
 * <code>AutoJoin</code> can be used any time a join is desired.  If
 * the server is not connected, it will wait until the server
 * connects.  If the server is connected, it will try to join right
 * away.</p>
 *
 * <h2>Example Usage</h2>
 * <p>You will probably want to at least use the
 * <code>AutoRegister</code> and <code>AutoResponder</code> classes.
 * Example:</p>
 *
 * <p>Note that in the example, the first line is optional.
 * <code>IRCConnection</code> can be called with its default
 * constructor.  See note below about why this is done.
 * <code>IRCConnection</code> will instantiate its own
 * <code>ClientState</code> object if you do not provide one.</p>
 * 
 * <pre>
 * ClientState clientState = new MyAppClientState();
 * IRCConnection connection = new IRCConnection( clientState );
 *
 * // AutoRegister and AutoResponder both add themselves to the
 * // appropriate observerables.  Both will remove themselves with the
 * // disable() method.
 * 
 * AutoRegister autoReg
 *   = new AutoRegister( "repp", "bdamm", "Ben Damm", connection );
 * AutoReconnect autoRecon = new AutoReconnect( connection );
 * AutoResponder autoRes = new AutoResponder( connection );
 *
 * // Very important that the objects above get added before the connect.
 * // If done otherwise, AutoRegister will throw an
 * // IllegalStateException, as AutoRegister can't catch the
 * // state switch to UNREGISTERED from UNCONNECTED.
 *
 * autoRecon.go( server, port );
 * </pre>
 *
 * <h2>Client State</h2>
 * <p>The <code>ClientStateMonitor</code> class tells commands to
 * change the client state when they are received.
 * <code>ClientStateMonitor</code> is automatically added to the
 * command queue before any other command, so that you can be
 * guaranteed that the <code>ClientState</code> is updated before any
 * observer sees a command.</p>
 *
 * <p>So, how does an application know when a channel has been joined,
 * a user has joined a channel we are already on, etc?  How does the
 * application get fine-grained access to client state change info?
 * This is a tricky question, and the current solution is to sublcass
 * the <code>clientstate.ClientState</code> and
 * <code>clientstate.Channel</code> classes with your own, overriding
 * the <code>setXxxxXxxx</code> methods.  Each method would call
 * <code>super.setXxxXxxx</code> and then proceed to change the
 * application as required.</p>
 *
 * <h2>Startup</h2>
 * <p>IRCConnection starts in the <code>UNCONNECTED</code> state and
 * makes no attempt to connect until the <code>connect(...)</code>
 * method is called.</p>
 *
 * <p><code>IRCConnection</code> starts a single thread at
 * construction time.  This thread simply waits for events.  An event
 * is a disconnection request or an incoming message.  Events are
 * dealt with by this thread.  If connect is called, a second thread
 * is created to listen for input from the server (InputHandler).
 *
 * @see f00f.net.irc.martyr.A_FAQ
 * @see f00f.net.irc.martyr.clientstate.ClientState
 * @see f00f.net.irc.martyr.services.AutoRegister
 * @see f00f.net.irc.martyr.services.AutoResponder
 * @see f00f.net.irc.martyr.State
 *
 */
/*
 * Event handling re-org
 *
 *  - A message is an event
 *  - A disconnect request is an event, placed on the queue?
 *  -- Off I go to do other stuff.
 */
public class IRCConnection {
    static Logger log = Logger.getLogger(IRCConnection.class.getName());

    public IRCConnection()
    {
        this( new ClientState() );
    }

    public IRCConnection( ClientState clientState )
    {
        // State observers are notified of state changes.
        // Command observers are sent a copy of each message that arrives.
        stateObservers = new StateObserver();
        commandObservers = new CommandObserver();
        this.clientState = clientState;
        stateQueue = new LinkedList<State>();

        commandRegister = new CommandRegister();
        commandSender = new DefaultCommandSender();

        setState( State.UNCONNECTED );

        new ClientStateMonitor( this );

        localEventQueue = new LinkedList<String>();

        eventThread = new EventThread();
        eventThread.setDaemon( true );
        startEventThread();
    }

    /**
     * This method exists so that subclasses may perform operations before
     * the event thread starts, but overriding this method.
     * */
    protected void startEventThread()
    {
        eventThread.start();
    }

    /**
     * In the event you want to stop martyr, call this.  This asks the
     * event thread to finish the current event, then die.
     * */
    public void stop()
    {
        eventThread.doStop();
    }

    /**
     * Performs a standard connection to the server and port. If we are already
     * connected, this just returns.
     *
     * @param server Server to connect to
     * @param port Port to connect to
     * @throws IOException if we could not connect
     */
    public void connect( String server, int port )
        throws IOException
    {
        synchronized( connectMonitor )
        {
            //log.debug("IRCConnection: Connecting to " + server + ":" + port);
            if( connected )
            {
                log.severe("IRCConnection: Connect requested, but we are already connected!");
                return;
            }

            connectUnsafe( new Socket( server, port ), server );
        }
    }

    /**
     * This allows the developer to provide a pre-connected socket, ready for use.
     * This is so that any options that the developer wants to set on the socket
     * can be set.  The server parameter is passed in, rather than using the
     * customSocket.getInetAddr() because a DNS lookup may be undesirable.  Thus,
     * the canonical server name, whatever that is, should be provided.  This is
     * then passed on to the client state.
     *
     * @param customSocket Custom socket that we will connect over
     * @param server Server to connect to
     * @throws IOException if we could not connect
     * @throws IllegalStateException if we are already connected.
     */
    public void connect( Socket customSocket, String server )
        throws IOException, IllegalStateException
    {
        synchronized( connectMonitor )
        {
            if( connected )
            {
                throw new IllegalStateException( "Connect requested, but we are already connected!" );
            }

            connectUnsafe( customSocket, server );
        }

    }

    /**
     * <p>Orders the socket to disconnect.  This doesn't actually disconnect, it
     * merely schedules an event to disconnect.  This way, pending incoming
     * messages may be processed before a disconnect actually occurs.</p>
     * <p>No errors are possible from the disconnect.  If you try to disconnect an
     * unconnected socket, your disconnect request will be silently ignored.</p>
     */
    public void disconnect()
    {
        synchronized( eventMonitor )
        {
            disconnectPending = true;
            eventMonitor.notifyAll();
        }
    }

    /**
     * Sets the daemon status on the threads that <code>IRCConnection</code>
     * creates.  Default is true, that is, new InputHandler threads are
     * daemon threads, although the event thread is always a daemon.  The
     * result is that as long as there is an active connection, the
     * program will keep running.
     *
     * @param daemon Set if we are to be treated like a daemon
     */
    public void setDaemon( boolean daemon )
    {
        this.daemon = daemon;
    }

    /**
     * Signal threads to stop, and wait for them to do so.
     * @param timeout *2 msec to wait at most for stop.
     *
     * */
    public void shutdown(long timeout)
    {
        // Note: UNTESTED!
        try
        {
            // 1) shut down the input thread.
            synchronized( inputHandlerMonitor )
            {
                if( inputHandler != null )
                {
                    inputHandler.signalShutdown();
                }
                synchronized( socketMonitor )
                {
                    if( socket != null )
                    {
                        try
                        {
                            socket.close();
                        }
                        catch (IOException e)
                        {
                            // surprising?
                        }
                    }
                }
                if( inputHandler != null )
                {
                    inputHandler.join(timeout);
                }
            }

            // 2) shut down the event thread.
            eventThread.shutdown();
            eventThread.join(timeout);
        }
        catch( InterruptedException ie )
        {
            // We got interrupted - while waiting for death.
            // Shame that.
        }
    }

    public String toString()
    {
        return "IRCConnection";
    }

    public void addStateObserver( Observer observer )
    {
        //log.debug("IRCConnection: Added state observer " + observer);
        stateObservers.addObserver( observer );
    }

    public void removeStateObserver( Observer observer )
    {
        //log.debug("IRCConnection: Removed state observer " + observer);
        stateObservers.deleteObserver( observer );
    }

    public void addCommandObserver( Observer observer )
    {
        //log.debug("IRCConnection: Added command observer " + observer);
        commandObservers.addObserver( observer );
    }

    public void removeCommandObserver( Observer observer )
    {
        //log.debug("IRCConnection: Removed command observer " + observer);
        commandObservers.deleteObserver( observer );
    }


    public State getState()
    {
        return state;
    }

    public ClientState getClientState()
    {
        return clientState;
    }

    /**
     * Accepts a command to be sent.  Sends the command to the
     * CommandSender.
     *
     * @param command Command we will send
     * */
    public void sendCommand( OutCommand command )
    {
        commandSender.sendCommand( command );
    }

    /**
     * @return the first class in a chain of OutCommandProcessors.
     * */
    public CommandSender getCommandSender()
    {
        return commandSender;
    }

    /**
     * @param sender sets the class that is responsible for sending commands.
     * */
    public void setCommandSender( CommandSender sender )
    {
        this.commandSender = sender;
    }

    /**
     * @return the local address to which the socket is bound.
     * */
    public InetAddress getLocalAddress()
    {
        return socket.getLocalAddress();
    }

    public String getRemotehost()
    {
        return clientState.getServer();
    }

    /**
     * Sets the time in milliseconds we wait after each command is sent.
     *
     * @param sleepTime Length of time to sleep between commands
     * */
    public void setSendDelay( int sleepTime )
    {
        this.sendDelay = sleepTime;
    }

    /**
     * @since 0.3.2
     * @return a class that can schedule timer tasks.
     * */
    public CronManager getCronManager()
    {
        if( cronManager == null )
            cronManager = new CronManager();
        return cronManager;
    }

    /**
     * Inserts into the event queue a command that was not directly
     * received from the server.
     *
     * @param fakeCommand Fake command to inject into incoming queue
     * */
    public void injectCommand( String fakeCommand )
    {
        synchronized( eventMonitor )
        {
            localEventQueue.add( fakeCommand );
            eventMonitor.notifyAll();
        }
    }

    // ===== package methods =============================================

    void socketError( IOException ioe )
    {
        //log.debug("Socket error called.");
        //log.debug("IRCConnection: The stack of the exception:", ioe);

        //log.log(Level.SEVERE, "Socket error", ioe);
        disconnect();
    }

    /**
     * Splits a raw IRC command into three parts, the prefix, identifier,
     * and parameters.
     * @param wholeString String to be parsed
     * @return a String array with 3 components, {prefix,ident,params}.
     * */
    public static String[] parseRawString( String wholeString )
    {
        String prefix = "";
        String identifier;
        String params = "";

        StringTokenizer tokens = new StringTokenizer( wholeString, " " );

        if( wholeString.charAt(0) == ':' )
        {
            prefix = tokens.nextToken();
            prefix = prefix.substring( 1, prefix.length() );
        }

        identifier = tokens.nextToken();

        if( tokens.hasMoreTokens() )
        {
            // The rest of the string
            params = tokens.nextToken("");
        }

        String[] result = new String[3];
        result[0] = prefix;
        result[1] = identifier;
        result[2] = params;

        return result;
    }

    /**
     * Given the three parts of an IRC command, generates an object to
     * represent that command.
     *
     * @param prefix Prefix of command object
     * @param identifier ID of command
     * @param params Params of command
     * @return An InCommand object for the given command object
     * */
    protected InCommand getCommandObject( String prefix, String identifier, String params )
    {
        InCommand command;

        // Remember that commands are also factories.
        InCommand commandFactory = commandRegister.getCommand( identifier );
        if( commandFactory == null )
        {
            if( UnknownError.isError( identifier ) )
            {
                command = new UnknownError( identifier );
                log.warning("IRCConnection: Using " + command);
            }
            else if( UnknownReply.isReply( identifier ) )
            {
                command = new UnknownReply( identifier );
                //log.warning("IRCConnection: Using " + command);
            }
            else
            {
                // The identifier doesn't map to a command.
                log.warning("IRCConnection: Unknown command");
                command = new UnknownCommand();
            }
        }
        else
        {
            command = commandFactory.parse( prefix, identifier, params);

            if( command == null )
            {
                log.severe("IRCConnection: CommandFactory[" + commandFactory + "] returned NULL");
                return null;
            }
            //log.debug("IRCConnection: Using " + command);
        }

        return command;
    }



    /**
     * Executed by the event thread.
     *
     * @param wholeString String to be parsed and handled
     * */
    void incomingCommand( String wholeString )
    {
        //log.info("IRCConnection: RCV = " + wholeString);

        // 1) Parse out the command
        String cmdBits[];

        try
        {
            cmdBits = parseRawString( wholeString );
        }
        catch( Exception e )
        {
            // So.. we can't process the command.
            // So we call the error handler.
            handleUnparsableCommand( wholeString, e );
            return;
        }

        String prefix = cmdBits[0];
        String identifier = cmdBits[1];
        String params = cmdBits[2];

        // 2) Fetch command from factory
        InCommand command = getCommandObject( prefix, identifier, params );
        command.setSourceString( wholeString );

        // Update the state and send out to commandObservers
        localCommandUpdate( command );
    }

    protected void handleUnparsableCommand( String wholeString, Exception e )
    {
        log.log(Level.SEVERE, "Unable to parse server message.", e );
    }

    /**
     * Called only in the event thread.
     *
     * @param command Command to update
     * */
    private void localCommandUpdate( InCommand command )
    {
        // 3) Change the connection state if required
        // This allows us to change from UNREGISTERED to REGISTERED and
        // possibly back.
        State cmdState = command.getState();
        if( cmdState != State.UNKNOWN && cmdState != getState() )
            setState( cmdState );

    // TODO: Bug here?

        // 4) Notify command observers
        try
        {
            commandObservers.setChanged();
            commandObservers.notifyObservers( command );
        }
        catch( Throwable e )
        {
            log.log(Level.SEVERE, "IRCConnection: Command notify failed.", e);
        }

    }

    // ===== private variables ===========================================

    /** Object used to send commands. */
    private CommandSender commandSender;

    private CronManager cronManager;

    /** State of the session. */
    private State state;

    /**
     * Client state (our name, nick, groups, etc). Stored here mainly
     * because there isn't anywhere else to stick it.
     */
    private ClientState clientState;

    /**
     * Maintains a list of classes observing the state and notifies them
     * when it changes.
     */
    private StateObserver stateObservers;

    /**
     * Maintains a list of classes observing commands when they come in.
     */
    private CommandObserver commandObservers;

    /**
     * The actual socket used for communication.
     */
    private Socket socket;

    /**
     * Monitor access to socket.
     * */
    private final Object socketMonitor = new Object();

    /**
     * We want to prevent connecting and disconnecting at the same time.
     */
    private final Object connectMonitor = new Object();

    /**
     * This object should be notified if we want the main thread to check for
     * events.  An event is either an incoming message or a disconnect request.
     * Sending commands to the server is synchronized by the eventMonitor.
     */
    private final Object eventMonitor = new Object();

    /**
     * This tells the processEvents() method to check if we should disconnect
     * after processing all incoming messages.
     */
    // Protected by:
    // inputHandlerMonitor
    // eventMonitor
    // connectMonitor
    private boolean disconnectPending = false;

    /**
     * The writer to use for output.
     */
    private BufferedWriter socketWriter;

    /**
     * Command register, contains a list of commands that can be received
     * by the server and have matching Command objects.
     */
    private CommandRegister commandRegister;

    /**
     * Maintains a handle on the input handler.
     */
    private InputHandler inputHandler;

    /**
     * Access control for the input handler.
     */
    private final Object inputHandlerMonitor = new Object();

    /**
     * State queue keeps a queue of requests to switch state.
     */
    private LinkedList<State> stateQueue;

    /**
     * localEventQueue allows events not received from the server to be
     * processed.
     * */
    private LinkedList<String> localEventQueue;

    /**
     * Setting state prevents setState from recursing in an uncontrolled
     * manner.
     */
    private boolean settingState = false;

    /**
     * Event thread waits for events and executes them.
     */
    private EventThread eventThread;

    /**
     * Determines the time to sleep every time we send a message.  We do this
     * so that the server doesn't boot us for flooding.
     */
    private int sendDelay = 300;

    /**
     * connected just keeps track of whether we are connected or not.
     */
    private boolean connected = false;

    /**
     * Are we set to be a daemon thread?
     */
    private boolean daemon = false;

    // ===== private methods =============================================

    /**
     * Unsafe, because this method can only be called by a method that has a lock
     * on connectMonitor.
     *
     * @param socket Socket to connect over
     * @param server Server to connect to
     * @throws IOException if connection fails
     */
    private void connectUnsafe( Socket socket, String server )
        throws IOException
    {
        synchronized(socketMonitor)
        {
            this.socket = socket;
        }

        socketWriter =
            new BufferedWriter( new OutputStreamWriter(    socket.getOutputStream() ) );

        /**
         * The reader to use for input.  Managed by the InputHandler.
         */
        BufferedReader socketReader =
            new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        // A simple thread that waits for input from the server and passes
        // it off to the IRCConnection class.
        //if( inputHandler != null )
        //{
        //    log.fatal("IRCConnection: Non-null input handler on connect!!");
        //    return;
        //}

        synchronized( inputHandlerMonitor )
        {
            // Pending events get processed after a disconnect call, and there
            // shouldn't be any events generated while disconnected, so it makes
            // sense to test for this condition.
            if( inputHandler != null && inputHandler.pendingMessages() )
            {
                log.severe("IRCConnection: Tried to connect, but there are pending messages!");
                return;
            }

            if( inputHandler != null && inputHandler.isAlive() )
            {
                log.severe("IRCConnection: Tried to connect, but the input handler is still alive!");
                return;
            }

            clientState.setServer( server );
            clientState.setPort( socket.getPort() );

            connected = true;

            inputHandler = new InputHandler( socketReader, this, eventMonitor );
            inputHandler.setDaemon( daemon );
            inputHandler.start();
        }
        setState( State.UNREGISTERED );
    }

    private class EventThread extends Thread
    {
        private boolean doShutdown = false;

        public EventThread()
        {
            super("EventThread");
        }

        public void run()
        {
            handleEvents();
        }

        public void shutdown()
        {
            synchronized(eventMonitor)
            {
                this.doShutdown = true;
                eventMonitor.notifyAll();
            }
        }

        private void handleEvents()
        {
            try
            {
                while( true )
                {
                    // Process all events in the event queue.
                    //log.debug("IRCConnection: Processing events");
                    while( processEvents() ) { }

                    // We can't process events while synchronized on the
                    // eventMonitor because we may end up in deadlock.
                    synchronized( eventMonitor )
                    {
                        if( !doShutdown && !pendingEvents() )
                        {
                            eventMonitor.wait();
                        }

                        if( doShutdown )
                        {
                            return;
                        }
                    }
                }
            }
            catch( InterruptedException ie )
            {
                log.log(Level.WARNING, "Interrupted while handling events.", ie );
                // And we do what?
                // We die, that's what we do.
            }
        }

        public void doStop()
        {
            shutdown();
        }

        public String toString()
        {
            return "EventThread";
        }
    }

    /**
     * This method synchronizes on the inputHandlerMonitor.  Note that if
     * additional event types are processed, they also need to be added to
     * pendingEvents().
     * @return true if events were processed, false if there were no events to
     * process.
     */
    private boolean processEvents()
    {
        boolean events = false;

        // the inputHandlerMonitor here serves two purposes: To protect
        // from inputHandler changes and to ensure only one thread is
        // operating in processEvents.
        //
        // Perhaps a different monitor should be used?
        synchronized( inputHandlerMonitor )
        {
            while( inputHandler != null && inputHandler.pendingMessages() )
            {
                String msg = inputHandler.getMessage();
                incomingCommand( msg );
                events = true;
            }

            while( localEventQueue != null && !localEventQueue.isEmpty() )
            {
                String msg = localEventQueue.removeFirst();
                incomingCommand( msg );
                events = true;
            }

            if( disconnectPending )
            {
                //log.debug("IRCConnection: Process events: Disconnect pending.");
                doDisconnect();
                events = true;
            }
        }

        return events;
    }

    /**
     * Does no synchronization on its own.  This does not synchronize on
     * any of the IRCConnection monitors or objects and returns after making a
     * minimum of method calls.
     * @return true if there are pending events that need processing.
     */
    private boolean pendingEvents()
    {
        if( inputHandler != null && inputHandler.pendingMessages() )
            return true;
        if( disconnectPending )
            return true;
        if( localEventQueue != null && !localEventQueue.isEmpty() )
            return true;

        return false;
    }

    // Synchronized by inputHandlerMonitor, called only from processEvents.
    private void doDisconnect()
    {
        synchronized( connectMonitor )
        {
            disconnectPending = false;

            if( !connected )
            {
                return;
            }
            connected = false;

            try
            {
                final long startTime = System.currentTimeMillis();
                final long sleepTime = 1000;
                final long stopTime = startTime + sleepTime;
                //log.debug("IRCConnection: Sleeping for a bit ("
                //    + sleepTime + ")..");
                // Slow things down a bit so the server doesn't kill us
                // Also, we want to give a second to let any pending messages
                // get processed and any pending disconnect() calls to be made.
                // It is important that we use wait instead of sleep!
                while( stopTime - System.currentTimeMillis() > 0 )
                {
                    connectMonitor.wait( stopTime - System.currentTimeMillis() );
                }
            }
            catch( InterruptedException ie )
            {
                // Ignore
            }

            //log.debug("IRCConnection: Stopping input handler.");
            // Deprecated?
            // inputHandler.stop();
            // inputHandler = null;

            //log.debug("IRCConnection: Closing socket.");
            try
            {
                socket.close();
            }
            catch( IOException ioe )
            {
                // And we are supposed to do what?
                // This probably means we've called disconnect on a closed
                // socket.
                handleSocketCloseException( ioe );
                return;
            }
            finally
            {
                connected = false;
            }
        }

        // The input handler should die, because we closed the socket.
        // We'll wait for it to die.
        synchronized( inputHandlerMonitor )
        {
            //log.debug("IRCConnection: Waiting for the input handler to die..");
            try
            {
                // log.debug("IRCConnection: Stack:");

                if( inputHandler.isAlive() )
                    inputHandler.join();
                else
                {
                    //log.debug("IRCConnection: No waiting required, input hander is already dead.");
                }
            }
            catch( InterruptedException ie )
            {
                //log.debug("IRCConnection: Error in join(): " + ie);
            }
            //log.debug("IRCConnection: Done waiting for the input handler to die.");
        }


        // There may be pending messages that we should process before we
        // actually notify all the state listeners.
        processEvents();

        // It is important that the state be switched last.  One of the
        // state listeners may try to re-connect us.
        setState( State.UNCONNECTED );

    }

    protected void handleSocketCloseException( IOException ioe )
    {
        log.log(Level.WARNING, "Error closing socket.", ioe );
    }

    /**
     * Signals to trigger a state change.  Won't actually send a state change
     * until a previous attempt at changing the state finishes.  This is
     * important if one of the state listeners affects the state (ie tries to
     * reconnect if we disconnect, etc).
     *
     * @param newState New state to set connection to.
     */
    private void setState( State newState )
    {
        if( settingState )
        {
            // We are already setting the state.  We want to complete changing
            // to one state before changing to another, so that we don't have
            // out-of-order state change signals.
            stateQueue.addLast( newState );
            return;
        }

        settingState = true;

        if( state == newState )
            return;

        while( true )
        {
            state = newState;

            //log.debug("IRCConnection: State switch: " + state);

            try
            {
                stateObservers.setChanged();
                stateObservers.notifyObservers( newState );
            }
            catch( Throwable e )
            {
                log.log(Level.SEVERE, "IRCConnection: State update failed.", e);
            }

            if( stateQueue.isEmpty() )
                break;
            newState = stateQueue.removeFirst();
        }

        settingState = false;
    }

    private class DefaultCommandSender implements CommandSender
    {
        public CommandSender getNextCommandSender()
        {
            return null;
        }

        public void sendCommand( OutCommand oc )
        {
            finalSendCommand( oc.render() );
        }
    }

    /**
     * Sends the command down the socket, with the required 'CRLF' on the
     * end.  Waits for a little bit after sending the command so that we don't
     * accidentally flood the server.
     *
     * @param str String to send
     */
    private void finalSendCommand( String str )
    {
        try
        {
            synchronized( eventMonitor )
            {
                //log.info("IRCConnection: SEND= " + str);

                if( disconnectPending )
                {
                    //log.debug("IRCConnection: Send cancelled, disconnect pending.");
                    return;
                }

                socketWriter.write( str + "\r\n" );
                socketWriter.flush();

                try
                {
                    // Slow things down a bit so the server doesn't kill us
                    // We do this after the send so that if the send fails the
                    // exception is handled right away.
                    Thread.sleep( sendDelay );
                }
                catch( InterruptedException ie )
                {
                    // Ignore
                }
            }
        }
        catch( IOException ioe )
        {
            socketError( ioe );
        }
    }
    // ----- END IRCConnection -------------------------------------------
}
