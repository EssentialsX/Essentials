package net.ess3.provider.providers;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.ess3.provider.AbstractChatEvent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;

public abstract class PaperChatListenerProvider implements Listener {
    private final LegacyComponentSerializer serializer;
    private final HashMap<AsyncChatEvent, PaperChatEvent> eventMap = new HashMap<>();

    public PaperChatListenerProvider() {
        this.serializer = LegacyComponentSerializer.builder()
                .flattener(ComponentFlattener.basic())
                .extractUrls(AbstractChatEvent.URL_PATTERN)
                .useUnusualXRepeatedCharacterHexFormat().build();
    }

    public abstract void onChatLowest(final AbstractChatEvent event);

    public abstract void onChatNormal(final AbstractChatEvent event);

    public abstract void onChatHighest(final AbstractChatEvent event);

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLowest(final AsyncChatEvent event) {
        onChatLowest(wrap(event));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onNormal(final AsyncChatEvent event) {
        onChatNormal(wrap(event));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHighest(final AsyncChatEvent event) {
        final PaperChatEvent paperChatEvent = wrap(event);
        onChatHighest(paperChatEvent);

        if (!event.isCancelled()) {
            final TextComponent format = serializer.deserialize(paperChatEvent.getFormat());
            final TextComponent eventMessage = serializer.deserialize(paperChatEvent.getMessage());

            event.renderer(ChatRenderer.viewerUnaware((player, displayName, message) ->
                    format.replaceText(builder -> builder
                            .match("%(\\d)\\$s").replacement((index, match) -> {
                                if (index.group(1).equals("1")) {
                                    return displayName;
                                }
                                return eventMessage;
                            })
                    )));
        }

        eventMap.remove(event);
    }

    private PaperChatEvent wrap(final AsyncChatEvent event) {
        PaperChatEvent paperChatEvent = eventMap.get(event);
        if (paperChatEvent != null) {
            return paperChatEvent;
        }

        paperChatEvent = new PaperChatEvent(event);
        eventMap.put(event, paperChatEvent);

        return paperChatEvent;
    }
}
