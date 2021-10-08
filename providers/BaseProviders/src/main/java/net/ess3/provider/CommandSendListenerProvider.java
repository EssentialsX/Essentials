package net.ess3.provider;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A provider for 1.13+ command send listeners.
 * <p>
 * Note to maintainers: this doesn't extend {@link ProviderListener} because it doesn't make sense here.
 */
public abstract class CommandSendListenerProvider implements Provider, Listener {
    private final Filter commandFilter;

    protected CommandSendListenerProvider(Filter commandFilter) {
        this.commandFilter = commandFilter;
    }

    protected final Predicate<String> filter(final Player player) {
        return commandFilter.apply(player);
    }

    /**
     * A function that returns a predicate to test whether commands should be hidden from the given player.
     */
    public interface Filter extends Function<Player, Predicate<String>> {
    }
}
