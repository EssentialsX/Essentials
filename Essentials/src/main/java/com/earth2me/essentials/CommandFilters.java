package com.earth2me.essentials;

import com.earth2me.essentials.config.ConfigurateUtil;
import com.earth2me.essentials.config.EssentialsConfiguration;
import net.ess3.api.IUser;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CommandFilters implements IConf {

    private final IEssentials ess;
    private final EssentialsConfiguration config;
    private Map<CommandFilter.Type, List<CommandFilter>> commandFilters;

    public CommandFilters(final IEssentials ess) {
        this.ess = ess;
        config = new EssentialsConfiguration(new File(ess.getDataFolder(), "command-filters.yml"), "/command-filters.yml");

        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        config.load();
        commandFilters = parseCommandFilters(config);
    }

    private Map<CommandFilter.Type, List<CommandFilter>> parseCommandFilters(EssentialsConfiguration config) {
        final Map<CommandFilter.Type, List<CommandFilter>> commandFilters = new EnumMap<>(CommandFilter.Type.class);
        final CommentedConfigurationNode filterSection = config.getSection("filters");
        for (final String filterItem : ConfigurateUtil.getKeys(filterSection)) {
            final CommentedConfigurationNode section = filterSection.node(filterItem);
            final String patternString = section.node("pattern").getString();
            final Pattern pattern = patternString == null ? null : compileRegex(patternString);
            final String command = section.node("command").getString();

            if (pattern == null && command == null) {
                ess.getLogger().warning("Invalid command filter '" + filterItem + "', filter must either define 'pattern' or 'command'!");
                continue;
            }

            if (pattern != null && command != null) {
                ess.getLogger().warning("Invalid command filter '" + filterItem + "', filter can't have both 'pattern' and 'command'!");
                continue;
            }

            Integer cooldown = section.node("cooldown").getInt(-1);
            if (cooldown < 0) {
                cooldown = null;
            } else {
                cooldown *= 1000; // Convert to milliseconds
            }

            final boolean persistentCooldown = section.node("persistent-cooldown").getBoolean(true);
            final BigDecimal cost = ConfigurateUtil.toBigDecimal(section.node("cost").getString(), null);

            final String filterItemName = filterItem.toLowerCase(Locale.ENGLISH);

            if (pattern == null) {
                commandFilters.computeIfAbsent(CommandFilter.Type.ESS, k -> new ArrayList<>()).add(new EssCommandFilter(filterItemName, command, compileRegex(command), cooldown, persistentCooldown, cost));
            } else {
                commandFilters.computeIfAbsent(CommandFilter.Type.REGEX, k -> new ArrayList<>()).add(new RegexCommandFilter(filterItemName, pattern, cooldown, persistentCooldown, cost));
            }
        }
        return commandFilters;
    }

    private Pattern compileRegex(String regex) {
        if (regex.startsWith("^")) {
            try {
                return Pattern.compile(regex.substring(1));
            } catch (final PatternSyntaxException e) {
                ess.getLogger().warning("Command cooldown error: " + e.getMessage());
                return null;
            }
        } else {
            // Escape above Regex
            if (regex.startsWith("\\^")) {
                regex = regex.substring(1);
            }
            final String cmd = regex.replaceAll("\\*", ".*"); // Wildcards are accepted as asterisk * as known universally.
            return Pattern.compile(cmd + "( .*)?"); // This matches arguments, if present, to "ignore" them from the feature.
        }
    }

    public EssentialsConfiguration getConfig() {
        return config;
    }

    public CommandFilter getCommandCooldown(final IUser user, final String label, CommandFilter.Type type) {
        if (user.isAuthorized("essentials.commandcooldowns.bypass")) return null;
        return getFilter(label, type, filter -> filter.hasCooldown() && !user.isAuthorized("essentials.commandcooldowns.bypass." + filter.getName()));
    }

    public CommandFilter getCommandCost(final IUser user, final String label, CommandFilter.Type type) {
        if (user.isAuthorized("essentials.nocommandcost.all")) return null;
        return getFilter(label, type, filter -> filter.hasCost() && !user.isAuthorized("essentials.nocommandcost." + filter.getName()));
    }

    private CommandFilter getFilter(final String label, CommandFilter.Type type, Predicate<CommandFilter> filterPredicate) {
        for (CommandFilter filter : commandFilters.get(type)) {
            if (!filterPredicate.test(filter)) continue;

            final boolean matches = filter.getPattern().matcher(label).matches();
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info(String.format("Checking command '%s' against filter '%s': %s", label, filter.getName(), matches));
            }

            if (matches) {
                return filter;
            }
        }
        return null;
    }
}
