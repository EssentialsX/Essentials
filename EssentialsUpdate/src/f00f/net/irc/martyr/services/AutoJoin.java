/*
 * Original version: Ben Damm <bdamm@dammfine.com>
 * Changes by: Morgan Christiansson <martyr@mog.se>
 * 	- Spotted bugs
 * 	- Added timer
 * 	- Responds to Invites
 *  - Re-tries a join with a bad key
 *
 * 	Note: Requires Java 1.4
 */
package f00f.net.irc.martyr.services;

import f00f.net.irc.martyr.GenericAutoService;
import f00f.net.irc.martyr.IRCConnection;
import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.State;
import f00f.net.irc.martyr.TimerTaskCommand;
import f00f.net.irc.martyr.clientstate.Channel;
import f00f.net.irc.martyr.commands.InviteCommand;
import f00f.net.irc.martyr.commands.JoinCommand;
import f00f.net.irc.martyr.commands.KickCommand;
import f00f.net.irc.martyr.errors.GenericJoinError;

/**
 * <p>AutoJoin joins a group if the IRCConnection is ready.  It will wait until
 * it is ready if it is not (by waiting for the REGISTERED state change).</p>
 *
 * <p>AutoJoin maintains a persistent Join (re-join if kicked).
 * AutoJoin can cease to be persistent by calling the 'disable'
 * method.</p>
 */
public class AutoJoin extends GenericAutoService
{
    //static Logger log = Logger.getLogger(AutoJoin.class);

    private String channel = null;
    private String key = null;
    private TimerTaskCommand joinTimerTask = null;
    private long joinTimerTaskDelay = 10*1000;

    public AutoJoin( IRCConnection connection, String channel )
    {
        this( connection, channel, null );
    }

    public AutoJoin( IRCConnection connection, String channel, String key )
    {
        super( connection );

        this.channel = channel;
        this.key = key;

        enable();

        updateState( connection.getState() );
    }

    protected void updateState( State state )
    {

        if( state == State.REGISTERED )
            performJoin();
    }

    protected void updateCommand( InCommand command_o )
    {
        if( command_o instanceof KickCommand )
        {
            KickCommand kickCommand = (KickCommand)command_o;

            if( kickCommand.kickedUs( getConnection().getClientState() ) )
            {
                if( Channel.areEqual(kickCommand.getChannel(), channel))
                {
                    performJoin();
                }
                else
                {
                    // mog: TODO: Should we really join a channel for which we aren't the AutoJoin:er?
                    // BD: You are quite right, this AutoJoin should only worry about itself.
                    // getConnection().sendCommand( new JoinCommand( kickCommand.getChannel() ) );
                }
            }
        }
        else if(command_o instanceof GenericJoinError )
        {
            GenericJoinError joinErr = (GenericJoinError)command_o;

            if( Channel.areEqual( joinErr.getChannel(), channel ) )
            {
                //log.debug("AutoJoin: Failed to join channel: "+joinErr.getComment());
                scheduleJoin();
            }
        }
        else if( command_o instanceof InviteCommand )
        {
            InviteCommand invite = (InviteCommand)command_o;
            if(!getConnection().getClientState().isOnChannel(invite.getChannel()))
            {
                performJoin();
            }
        }
    }

    /**
     * Sets up and sends the join command.
     * */
    protected synchronized void performJoin()
    {
        setupJoin();
        sendJoinCommand();
    }

    /**
     * Performs various tasks immediatly prior to sending a join command.
     * Called from performJoin.
     * */
    protected void setupJoin()
    {
        if(joinTimerTask != null)
        {
            joinTimerTask.cancel();
            joinTimerTask = null;
        }
    }

    /**
     * This method sends the actual command.  Called from performJoin.
     * */
    protected void sendJoinCommand()
    {
        getConnection().sendCommand( new JoinCommand( channel, key ) );
    }

    protected void scheduleJoin()
    {
        if(joinTimerTask == null || !joinTimerTask.isScheduled())
        {
            joinTimerTask = new TimerTaskCommand(getConnection(), new JoinCommand(channel, key));
            //TODO back off delay on repeated retries?
            getConnection().getCronManager().schedule(joinTimerTask, joinTimerTaskDelay);
        }
    }

    public String toString()
    {
        if( key == null )
            return "AutoJoin [" + channel + "]";
        return "AutoJoin [" + channel + "," + key + "]";
    }

    // END AutoResponder
}
 


 
