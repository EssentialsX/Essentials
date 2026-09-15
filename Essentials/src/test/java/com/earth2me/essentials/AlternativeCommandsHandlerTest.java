package com.earth2me.essentials;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AlternativeCommandsHandlerTest {

    @Test
    void returnsNullWhenAllAlternativeCommandsHaveBeenCollected() throws Exception {
        final IEssentials essentials = mock(IEssentials.class);
        final Server server = mock(Server.class);
        final PluginManager pluginManager = mock(PluginManager.class);
        final ISettings settings = mock(ISettings.class);
        when(essentials.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(pluginManager.getPlugins()).thenReturn(new Plugin[0]);
        when(essentials.getSettings()).thenReturn(settings);
        when(settings.isDebug()).thenReturn(false);

        final AlternativeCommandsHandler handler = new AlternativeCommandsHandler(essentials);
        final Field field = AlternativeCommandsHandler.class.getDeclaredField("altCommands");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        final Map<String, List<WeakReference<Command>>> alternatives =
                (Map<String, List<WeakReference<Command>>>) field.get(handler);
        alternatives.put("foo", new ArrayList<>(Arrays.asList(
                new WeakReference<Command>(null),
                new WeakReference<Command>(null)
        )));

        assertNull(handler.getAlternative("foo"));
    }
}
