package com.earth2me.essentials.adventure;

import net.ess3.provider.AbstractChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class PaperAdventureFacet implements AdventureFacet {
    private final LegacyComponentSerializer legacySerializer;
    private final MiniMessage miniMessageNoTags;
    private final MiniMessage miniMessageInstance;

    private final String primaryColor;
    private final String secondaryColor;

    public PaperAdventureFacet(final String primaryColor, final String secondaryColor) {
        this.primaryColor = primaryColor != null ? primaryColor : "gold";
        this.secondaryColor = secondaryColor != null ? secondaryColor : "red";

        final LegacyComponentSerializer.Builder builder = LegacyComponentSerializer.builder()
                .flattener(ComponentFlattener.basic())
                .extractUrls(AbstractChatEvent.URL_PATTERN)
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat();
        legacySerializer = builder.build();

        miniMessageNoTags = MiniMessage.builder().strict(true).build();

        miniMessageInstance = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolvers(TagResolver.standard())
                        .resolver(TagResolver.resolver("primary", supplyTag(true)))
                        .resolver(TagResolver.resolver("secondary", supplyTag(false)))
                        .build())
                .build();
    }

    private Tag supplyTag(final boolean primary) {
        final String color = primary ? primaryColor : secondaryColor;

        final TextColor textColor;
        if (color.startsWith("#")) {
            textColor = TextColor.color(Color.fromRGB(Integer.decode(color)).asRGB());
        } else {
            textColor = NamedTextColor.NAMES.value(color.toLowerCase(Locale.ENGLISH));
        }

        return textColor != null ? Tag.styling(textColor) : Tag.styling(primary ? NamedTextColor.GOLD : NamedTextColor.RED);
    }

    @Override
    public ComponentHolder deserializeMiniMessage(String message) {
        return new ComponentHolder(miniMessageInstance.deserialize(message));
    }

    @Override
    public void send(CommandSender sender, ComponentHolder component) {
        sender.sendMessage((Component) component.getComponent());
    }

    @Override
    public void send(Player player, ComponentHolder component) {
        player.sendMessage((Component) component.getComponent());
    }

    @Override
    public String legacyToMini(String message) {
        return legacyToMini(message, true);
    }

    @Override
    public String legacyToMini(String message, boolean useCustomTags) {
        final Component deserializedText = legacySerializer.deserialize(message);
        if (useCustomTags) {
            return miniMessageInstance.serialize(deserializedText);
        } else {
            return miniMessageNoTags.serialize(deserializedText);
        }
    }

    @Override
    public String escapeTags(String input) {
        return miniMessageInstance.escapeTags(input);
    }

    @Override
    public String stripTags(String input) {
        return miniMessageInstance.stripTags(input);
    }

    @Override
    public ComponentHolder legacyToAdventure(String message) {
        return new ComponentHolder(legacySerializer.deserialize(message));
    }

    @Override
    public ComponentHolder text(String message) {
        return new ComponentHolder(Component.text(message));
    }

    @Override
    public String miniToLegacy(String message) {
        return adventureToLegacy(miniMessageInstance.deserialize(message));
    }

    @Override
    public String adventureToLegacy(ComponentHolder component) {
        return adventureToLegacy((Component) component.getComponent());
    }

    private String adventureToLegacy(Component component) {
        return legacySerializer.serialize(component);
    }

    @Override
    public ComponentHolder append(ComponentHolder base, ComponentHolder... addition) {
        Component baseComponent = (Component) base.getComponent();
        for (ComponentHolder holder : addition) {
            final Component additionComponent = (Component) holder.getComponent();
            baseComponent = baseComponent.append(additionComponent);
        }
        return new ComponentHolder(baseComponent);
    }

    @Override
    public void close() {
    }
}
