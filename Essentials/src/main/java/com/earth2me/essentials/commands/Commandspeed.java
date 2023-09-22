package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FloatUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandspeed extends EssentialsCommand {
    private static final List<String> types = Arrays.asList("walk", "fly", "1", "1.5", "1.75", "2");
    private static final List<String> speeds = Arrays.asList("1", "1.5", "1.75", "2");

    public Commandspeed() {
        super("speed");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }
        speedOtherPlayers(server, sender, isFlyMode(args[0]), true, getMoveSpeed(args[1]), args[2]);
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final boolean isFly;
        final float speed;
        final boolean isBypass = user.isAuthorized("essentials.speed.bypass");
        if (args.length == 1) {
            isFly = flyPermCheck(user, user.getBase().isFlying());
            speed = getMoveSpeed(args[0]);
        } else {
            isFly = flyPermCheck(user, isFlyMode(args[0]));
            speed = getMoveSpeed(args[1]);
            if (args.length > 2 && user.isAuthorized("essentials.speed.others")) {
                if (args[2].trim().length() < 2) {
                    throw new PlayerNotFoundException();
                }
                speedOtherPlayers(server, user.getSource(), isFly, isBypass, speed, args[2]);
                return;
            }
        }

        if (isFly) {
            user.getBase().setFlySpeed(getRealMoveSpeed(speed, true, isBypass));
            user.sendMessage(tl("moveSpeed", tl("flying"), speed, user.getName()));
            return;
        }
        user.getBase().setWalkSpeed(getRealMoveSpeed(speed, false, isBypass));
        user.sendMessage(tl("moveSpeed", tl("walking"), speed, user.getName()));
    }

    private void speedOtherPlayers(final Server server, final CommandSource sender, final boolean isFly, final boolean isBypass, final float speed, final String name) throws PlayerNotFoundException {
        final boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).canInteractVanished();
        boolean foundUser = false;
        final List<Player> matchedPlayers = server.matchPlayer(name);
        for (final Player matchPlayer : matchedPlayers) {
            final User player = ess.getUser(matchPlayer);
            if (skipHidden && player.isHidden(sender.getPlayer()) && player.isHiddenFrom(sender.getPlayer())) {
                continue;
            }
            foundUser = true;
            if (isFly) {
                matchPlayer.setFlySpeed(getRealMoveSpeed(speed, true, isBypass));
                sender.sendMessage(tl("moveSpeed", tl("flying"), speed, matchPlayer.getName()));
            } else {
                matchPlayer.setWalkSpeed(getRealMoveSpeed(speed, false, isBypass));
                sender.sendMessage(tl("moveSpeed", tl("walking"), speed, matchPlayer.getName()));
            }
        }
        if (!foundUser) {
            throw new PlayerNotFoundException();
        }
    }

    private Boolean flyPermCheck(final User user, final boolean input) {
        final boolean canFly = user.isAuthorized("essentials.speed.fly");
        final boolean canWalk = user.isAuthorized("essentials.speed.walk");
        if (input && canFly || !input && canWalk || !canFly && !canWalk) {
            return input;
        } else return !canWalk;
    }

    private boolean isFlyMode(final String modeString) throws NotEnoughArgumentsException {
        final boolean isFlyMode;
        if (modeString.contains("fly") || modeString.equalsIgnoreCase("f")) {
            isFlyMode = true;
        } else if (modeString.contains("walk") || modeString.contains("run") || modeString.equalsIgnoreCase("w") || modeString.equalsIgnoreCase("r")) {
            isFlyMode = false;
        } else {
            throw new NotEnoughArgumentsException();
        }
        return isFlyMode;
    }

    private float getMoveSpeed(final String moveSpeed) throws NotEnoughArgumentsException {
        float userSpeed;
        try {
            userSpeed = FloatUtil.parseFloat(moveSpeed);
            if (userSpeed > 10f) {
                userSpeed = 10f;
            } else if (userSpeed < 0.0001f) {
                userSpeed = 0.0001f;
            }
        } catch (final NumberFormatException e) {
            throw new NotEnoughArgumentsException();
        }
        return userSpeed;
    }

    private float getRealMoveSpeed(final float userSpeed, final boolean isFly, final boolean isBypass) {
        final float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1f;
        if (!isBypass) {
            maxSpeed = (float) (isFly ? ess.getSettings().getMaxFlySpeed() : ess.getSettings().getMaxWalkSpeed());
        }

        if (userSpeed < 1f) {
            return defaultSpeed * userSpeed;
        } else {
            final float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return types;
        } else if (args.length == 2) {
            return speeds;
        } else if (args.length == 3 && sender.isAuthorized("essentials.speed.others", ess)) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
