package net.ess3.provider.providers;

import io.papermc.paper.text.PaperComponents;
import net.ess3.provider.SignUpdateProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;
import java.util.List;

public class PaperSignUpdateProvider implements SignUpdateProvider {
    private final PlainTextComponentSerializer serializer = PaperComponents.plainTextSerializer();

    @Override
    public boolean updateSign(final Sign sign, final Player player, final String[] lines) {
        final List<Component> components = new ArrayList<>();

        for (String line : lines) {
            components.add(serializer.deserialize(line));
        }

        final SignChangeEvent event = new SignChangeEvent(sign.getBlock(), player, components);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        for (int i = 0; i < components.size(); i++) {
            sign.line(i, components.get(i));
        }

        sign.update();
        return true;
    }
}
