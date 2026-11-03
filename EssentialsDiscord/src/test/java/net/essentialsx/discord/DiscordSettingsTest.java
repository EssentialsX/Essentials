package net.essentialsx.discord;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscordSettingsTest {
    @TempDir
    Path dataFolder;

    @Test
    public void testConfiguredPrivateMessageFormat() throws IOException {
        writeConfig("messages:\n  pms-to-discord: \"[Privado] {sender-displayname} -> {receiver-displayname}: {message}\"\n");
        final DiscordSettings settings = new DiscordSettings(plugin());
        assertEquals("[Privado] Sender -> Receiver: Hello", settings.getPmToDiscordFormat().format(
            new Object[]{"sender", "Sender", "receiver", "Receiver", "Hello"}));
    }

    @Test
    public void testDefaultPrivateMessageFormat() throws IOException {
        writeConfig("messages: {}\n");
        final DiscordSettings settings = new DiscordSettings(plugin());
        assertEquals("[SocialSpy] sender -> receiver: Hello", settings.getPmToDiscordFormat().format(
            new Object[]{"sender", "Sender", "receiver", "Receiver", "Hello"}));
    }

    @Test
    public void testReloadPrivateMessageFormat() throws IOException {
        writeConfig("messages:\n  pms-to-discord: \"First: {message}\"\n");
        final DiscordSettings settings = new DiscordSettings(plugin());
        assertEquals("First: Hello", settings.getPmToDiscordFormat().format(
            new Object[]{"sender", "Sender", "receiver", "Receiver", "Hello"}));
        writeConfig("messages:\n  pms-to-discord: \"It's {sender-username}: {message}\"\n");
        settings.reloadConfig();
        assertEquals("It's sender: Hello", settings.getPmToDiscordFormat().format(
            new Object[]{"sender", "Sender", "receiver", "Receiver", "Hello"}));
    }

    private EssentialsDiscord plugin() {
        final EssentialsDiscord plugin = mock(EssentialsDiscord.class);
        when(plugin.getDataFolder()).thenReturn(dataFolder.toFile());
        return plugin;
    }

    private void writeConfig(final String contents) throws IOException {
        Files.write(dataFolder.resolve("config.yml"), contents.getBytes(StandardCharsets.UTF_8));
    }
}
