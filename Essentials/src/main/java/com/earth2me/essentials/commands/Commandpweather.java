package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.WeatherType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

import static com.earth2me.essentials.I18n.tl;

public class Commandpweather extends EssentialsLoopCommand {
    private static final List<String> getAliases = Arrays.asList("get", "list", "show", "display");
    private static final Map<String, WeatherType> weatherAliases = new HashMap<>();

    static {
        weatherAliases.put("sun", WeatherType.CLEAR);
        weatherAliases.put("clear", WeatherType.CLEAR);
        weatherAliases.put("storm", WeatherType.DOWNFALL);
        weatherAliases.put("thunder", WeatherType.DOWNFALL);
    }

    public Commandpweather() {
        super("pweather");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0 || getAliases.contains(args[0].toLowerCase())) {
            if (args.length > 1) { // /pweather get md_5 || /pweather get *
                if (args[1].equals("*") || args[1].equals("**")) {
                    sender.sendMessage(tl("pWeatherPlayers"));
                }
                loopOnlinePlayersConsumer(server, sender, false, true, args[1], player -> getUserWeather(sender, player));
                return;
            }

            if (args.length == 1 || sender.isPlayer()) { // /pweather get
                if (sender.isPlayer()) {
                    getUserWeather(sender, sender.getUser(ess));
                    return;
                }
                throw new NotEnoughArgumentsException(); // We cannot imply the target for console
            }

            // Default to showing the weather of all online users for console when no arguments are provided
            if (ess.getOnlinePlayers().size() > 1) {
                sender.sendMessage(tl("pWeatherPlayers"));
            }
            for (final User player : ess.getOnlineUsers()) {
                getUserWeather(sender, player);
            }
        }

        if (args.length > 1 && !sender.isAuthorized("essentials.pweather.others", ess) && !args[1].equalsIgnoreCase(sender.getSelfSelector())) {
            sender.sendMessage(tl("pWeatherOthersPermission"));
            return;
        }

        final String weather = args[0].toLowerCase();
        if (!weatherAliases.containsKey(weather) && !weather.equalsIgnoreCase("reset")) {
            throw new NotEnoughArgumentsException(tl("pWeatherInvalidAlias"));
        }

        final StringJoiner joiner = new StringJoiner(", ");
        loopOnlinePlayersConsumer(server, sender, false, true, args.length > 1 ? args[1] : sender.getSelfSelector(), player -> {
            setUserWeather(player, weather);
            joiner.add(player.getName());
        });

        if (weather.equalsIgnoreCase("reset")) {
            sender.sendMessage(tl("pWeatherReset", joiner.toString()));
            return;
        }

        sender.sendMessage(tl("pWeatherSet", weather, joiner.toString()));
    }

    private void getUserWeather(final CommandSource sender, final IUser user) {
        if (user == null) {
            return;
        }

        if (user.getBase().getPlayerWeather() == null) {
            sender.sendMessage(tl("pWeatherNormal", user.getName()));
            return;
        }
        sender.sendMessage(tl("pWeatherCurrent", user.getName(), user.getBase().getPlayerWeather().toString().toLowerCase(Locale.ENGLISH)));
    }

    private void setUserWeather(final User user, final String weatherType) {
        if (weatherType.equalsIgnoreCase("reset")) {
            user.getBase().resetPlayerWeather();
            return;
        }
        user.getBase().setPlayerWeather(weatherAliases.get(weatherType));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("get", "reset", "storm", "sun");
        } else if (args.length == 2 && (getAliases.contains(args[0]) || user == null || user.isAuthorized("essentials.pweather.others"))) {
            return getPlayers(server, user);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User user, final String[] args) {
    }
}
