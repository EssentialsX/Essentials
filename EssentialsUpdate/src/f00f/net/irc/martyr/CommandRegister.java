package f00f.net.irc.martyr;

import java.util.Hashtable;

import f00f.net.irc.martyr.commands.*;
import f00f.net.irc.martyr.errors.*;
import f00f.net.irc.martyr.replies.*;

/**
 * CommandRegister is basically a big hashtable that maps IRC
 * identifiers to command objects that can be used as factories to
 * do self-parsing.  CommandRegister is also the central list of
 * commands.
 */
public class CommandRegister
{

    private Hashtable<String,InCommand> commands;
    public CommandRegister()
    {
        commands = new Hashtable<String,InCommand>();

        // Note that currently, we only have to register commands that
        // can be received from the server.
        new InviteCommand().selfRegister( this );
        new JoinCommand().selfRegister( this );
        new KickCommand().selfRegister( this );
        new MessageCommand().selfRegister( this );
        new ModeCommand().selfRegister( this );
        new IsonCommand().selfRegister( this );
        new NickCommand().selfRegister( this );
        new NoticeCommand().selfRegister( this );
        new PartCommand().selfRegister( this );
        new PingCommand().selfRegister( this );
        new QuitCommand().selfRegister( this );
        new TopicCommand().selfRegister( this );
        new WelcomeCommand().selfRegister( this );

        // Register errors
        new AlreadyRegisteredError().selfRegister( this );
        new CannotSendToChanError().selfRegister( this );
        new CantKillServerError().selfRegister( this );
        new ChannelBannedError().selfRegister( this );
        new ChannelInviteOnlyError().selfRegister( this );
        new ChannelLimitError().selfRegister( this );
        new ChannelWrongKeyError().selfRegister( this );
        new ChanOPrivsNeededError().selfRegister( this );
        new ErroneusNicknameError().selfRegister( this );
        new FileErrorError().selfRegister( this );
        new KeySetError().selfRegister( this );
        new LoadTooHighError().selfRegister( this );
        new NeedMoreParamsError().selfRegister( this );
        new NickCollisionError().selfRegister( this );
        new NickInUseError().selfRegister( this );
        new NoAdminInfoError().selfRegister( this );
        new NoLoginError().selfRegister( this );
        new NoMotdError().selfRegister( this );
        new NoNicknameGivenError().selfRegister( this );
        new NoOperHostError().selfRegister( this );
        new NoOriginError().selfRegister( this );
        new NoPermForHostError().selfRegister( this );
        new NoPrivilegesError().selfRegister( this );
        new NoRecipientError().selfRegister( this );
        new NoSuchChannelError().selfRegister( this );
        new NoSuchNickError().selfRegister( this );
        new NoSuchServerError().selfRegister( this );
        new NoTextToSendError().selfRegister( this );
        new NotOnChannelError().selfRegister( this );
        new NotRegisteredError().selfRegister( this );
        new PasswdMismatchError().selfRegister( this );
        new SummonDisabledError().selfRegister( this );
        new TooManyChannelsError().selfRegister( this );
        new TooManyTargetsError().selfRegister( this );
        new UModeUnknownFlagError().selfRegister( this );
        new UnknownCommandError().selfRegister( this );
        new UnknownModeError().selfRegister( this );
        new UserNotInChannelError().selfRegister( this );
        new UserOnChannelError().selfRegister( this );
        new UsersDisabledError().selfRegister( this );
        new UsersDontMatchError().selfRegister( this );
        new WasNoSuchNickError().selfRegister( this );
        new WildTopLevelError().selfRegister( this );
        new YoureBannedCreepError().selfRegister( this );

        // Register replies
        new ChannelCreationReply().selfRegister( this );
        new AwayReply().selfRegister( this );
        new ListEndReply().selfRegister( this );
        new ListReply().selfRegister( this );
        new ListStartReply().selfRegister( this );
        new LUserClientReply().selfRegister( this );
        new LUserMeReply().selfRegister( this );
        new LUserOpReply().selfRegister( this );
        new ModeReply().selfRegister( this );
        new NamesEndReply().selfRegister( this );
        new NamesReply().selfRegister( this );
        new NowAwayReply().selfRegister( this );
        new TopicInfoReply().selfRegister( this );
        new UnAwayReply().selfRegister( this );
        new WhoisChannelsReply().selfRegister( this );
        new WhoisEndReply().selfRegister( this );
        new WhoisIdleReply().selfRegister( this );
        new WhoisServerReply().selfRegister( this );
        new WhoisUserReply().selfRegister( this );
    }

    public void addCommand( String ident, InCommand command )
    {
        commands.put( ident, command );
    }

    public InCommand getCommand( String ident )
    {
        return commands.get( ident );
    }

}

