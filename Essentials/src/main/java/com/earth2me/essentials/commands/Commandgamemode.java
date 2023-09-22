package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import com.google.common.collect.ImmutableList;
import org.bukkit.GameMode;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;

public class Commandgamemode extends EssentialsLoopCommand {
    private final List<String> STANDARD_OPTIONS = ImmutableList.of("creative", "survival", "adventure", "spectator", "toggle");

    public Commandgamemode() {
        super("gamemode");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        } else if (args.length == 1) {
            loopOnlinePlayersConsumer(server, sender, false, true, args[0], user -> setUserGamemode(sender, matchGameMode(commandLabel), user));
        } else if (args.length == 2) {
            loopOnlinePlayersConsumer(server, sender, false, true, args[1], user -> setUserGamemode(sender, matchGameMode(args[0]), user));
        }

    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        GameMode gameMode;
        if (args.length == 0) {
            gameMode = matchGameMode(commandLabel);
        } else if (args.length > 1 && args[1].trim().length() > 2 && user.isAuthorized("essentials.gamemode.others")) {
            loopOnlinePlayersConsumer(server, user.getSource(), false, true, args[1], player -> setUserGamemode(user.getSource(), matchGameMode(args[0].toLowerCase(Locale.ENGLISH)), player));
            return;
        } else {
            try {
                gameMode = matchGameMode(args[0].toLowerCase(Locale.ENGLISH));
            } catch (final NotEnoughArgumentsException e) {
                if (user.isAuthorized("essentials.gamemode.others")) {
                    loopOnlinePlayersConsumer(server, user.getSource(), false, true, args[0], player -> setUserGamemode(user.getSource(), matchGameMode(commandLabel), player));
                    return;
                }
                throw new NotEnoughArgumentsException();
            }
        }

        if (gameMode == null) {
            gameMode = user.getBase().getGameMode() == GameMode.SURVIVAL ? GameMode.CREATIVE : user.getBase().getGameMode() == GameMode.CREATIVE ? GameMode.ADVENTURE : GameMode.SURVIVAL;
        }

        if (isProhibitedChange(user, gameMode)) {
            user.sendMessage(tl("cantGamemode", gameMode.name()));
            return;
        }

        user.getBase().setGameMode(gameMode);
        user.sendMessage(tl("gameMode", tl(user.getBase().getGameMode().toString().toLowerCase(Locale.ENGLISH)), user.getName()));
    }

    private void setUserGamemode(final CommandSource sender, final GameMode gameMode, final User user) throws NotEnoughArgumentsException {
        if (gameMode == null) {
            throw new NotEnoughArgumentsException(tl("gameModeInvalid"));
        }

        if (sender.isPlayer() && isProhibitedChange(sender.getUser(ess), gameMode)) {
            sender.sendMessage(tl("cantGamemode", gameMode.name()));
            return;
        }

        user.getBase().setGameMode(gameMode);
        sender.sendMessage(tl("gameMode", tl(gameMode.toString().toLowerCase(Locale.ENGLISH)), user.getName()));
    }

    // essentials.gamemode will let them change to any but essentials.gamemode.survival would only let them change to survival.
    private boolean isProhibitedChange(final IUser user, final GameMode to) {
        return user != null && !user.isAuthorized("essentials.gamemode.all") && !user.isAuthorized("essentials.gamemode." + to.name().toLowerCase());
    }

    private GameMode matchGameMode(String modeString) throws NotEnoughArgumentsException {
        GameMode mode = null;
        modeString = modeString.toLowerCase();
        if (modeString.equalsIgnoreCase("gmc") || modeString.equalsIgnoreCase("egmc") || modeString.contains("creat") || modeString.equalsIgnoreCase("1") || modeString.equalsIgnoreCase("c")) {
            mode = GameMode.CREATIVE;
        } else if (modeString.equalsIgnoreCase("gms") || modeString.equalsIgnoreCase("egms") || modeString.contains("survi") || modeString.equalsIgnoreCase("0") || modeString.equalsIgnoreCase("s")) {
            mode = GameMode.SURVIVAL;
        } else if (modeString.equalsIgnoreCase("gma") || modeString.equalsIgnoreCase("egma") || modeString.contains("advent") || modeString.equalsIgnoreCase("2") || modeString.equalsIgnoreCase("a")) {
            mode = GameMode.ADVENTURE;
        } else if (modeString.equalsIgnoreCase("gmsp") || modeString.equalsIgnoreCase("egmsp") || modeString.contains("spec") || modeString.equalsIgnoreCase("3") || modeString.equalsIgnoreCase("sp")) {
            mode = GameMode.SPECTATOR;
        } else if (!modeString.equalsIgnoreCase("gmt") && !modeString.equalsIgnoreCase("egmt") && !modeString.contains("toggle") && !modeString.contains("cycle") && !modeString.equalsIgnoreCase("t")) {
            throw new NotEnoughArgumentsException();
        }
        return mode;
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            try {
                // Direct command?  Don't ask for the mode
                matchGameMode(commandLabel);
                return getPlayers(server, sender);
            } catch (final NotEnoughArgumentsException e) {
                return STANDARD_OPTIONS;
            }
        } else if (args.length == 2) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        boolean isDirectGamemodeCommand;
        try {
            // Direct command?
            matchGameMode(commandLabel);
            isDirectGamemodeCommand = true;
        } catch (final NotEnoughArgumentsException ex) {
            isDirectGamemodeCommand = false;
        }
        if (args.length == 1) {
            if (user.isAuthorized("essentials.gamemode.others") && isDirectGamemodeCommand) {
                return getPlayers(server, user);
            } else {
                return STANDARD_OPTIONS;
            }
        } else if (args.length == 2 && user.isAuthorized("essentials.gamemode.others") && !isDirectGamemodeCommand) {
            return getPlayers(server, user);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User user, final String[] args) {

    }
}
