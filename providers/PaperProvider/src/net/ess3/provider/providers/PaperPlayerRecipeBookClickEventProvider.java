package net.ess3.provider.providers;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import net.ess3.provider.EventProvider;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

import java.util.function.Consumer;

public class PaperPlayerRecipeBookClickEventProvider extends EventProvider {
    public PaperPlayerRecipeBookClickEventProvider(Consumer<Event> function) {
        super(function);
    }

    @EventHandler
    public void onPlayerRecipeBookClick(PlayerRecipeBookClickEvent event) {
        function.accept(event);
    }

    @Override
    public String getDescription() {
        return "Paper Player Recipe Book Click Event Provider";
    }
}
