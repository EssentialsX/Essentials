package net.essentialsx;

import io.papermc.paper.event.player.PlayerServerFullCheckEvent;
import net.ess3.provider.AbstractChatEvent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class PaperAdventureSmuggler {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
            .flattener(ComponentFlattener.basic())
            .extractUrls(AbstractChatEvent.URL_PATTERN)
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .hexColors()
            .build();

    private PaperAdventureSmuggler() {
    }

    public static void smugglePlayerServerFullCheckEvent(final PlayerServerFullCheckEvent event, final String legacyMessage) {
        event.deny(LEGACY_COMPONENT_SERIALIZER.deserialize(legacyMessage));
    }
}
