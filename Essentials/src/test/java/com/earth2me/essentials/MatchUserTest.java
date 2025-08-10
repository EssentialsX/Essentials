package com.earth2me.essentials;

import com.earth2me.essentials.commands.PlayerNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatchUserTest {
    private Essentials ess;
    private ServerMock server;

    @BeforeEach
    public void setUp() {
        this.server = MockBukkit.mock();
        Essentials.TESTING = true;
        ess = MockBukkit.load(Essentials.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void exactMatchPreferredOverPartialOnline() throws Exception {
        final PlayerMock exactPlayer = server.addPlayer("Ginshin");
        final PlayerMock partialPlayer = server.addPlayer("Ginshin_BOT");
        final PlayerMock callerBase = server.addPlayer("Caller");

        final User caller = ess.getUser(callerBase);
        ess.getUser(exactPlayer);
        ess.getUser(partialPlayer);

        final User matched = ess.matchUser(server, caller, "Ginshin", false, false);
        assertEquals("Ginshin", matched.getName());
    }

    @Test
    public void hiddenPlayerExactOnlyWhenOfflineLookupPlayerCaller() throws Exception {
        final PlayerMock hiddenBase = server.addPlayer("Hidden");
        final PlayerMock callerBase = server.addPlayer("Caller");

        final User hidden = ess.getUser(hiddenBase);
        final User caller = ess.getUser(callerBase);

        hidden.setVanished(true);

        // Without offline-capable lookup, hidden target should not be found
        assertThrows(PlayerNotFoundException.class,
                () -> ess.matchUser(server, caller, "Hidden", false, false));

        // With offline-capable lookup, only exact matches should return the hidden user
        assertThrows(PlayerNotFoundException.class,
                () -> ess.matchUser(server, caller, "Hid", false, true));

        final User matched = ess.matchUser(server, caller, "Hidden", false, true);
        assertEquals("Hidden", matched.getName());
    }

    @Test
    public void hiddenPlayerExactOnlyWhenOfflineLookupConsoleCaller() throws Exception {
        final PlayerMock hiddenBase = server.addPlayer("HiddenTwo");
        final User hidden = ess.getUser(hiddenBase);

        hidden.setHidden(true);

        // Console caller represented by null source user
        assertThrows(PlayerNotFoundException.class,
                () -> ess.matchUser(server, null, "HiddenT", false, true));

        final User matched = ess.matchUser(server, null, "HiddenTwo", false, true);
        assertEquals("HiddenTwo", matched.getName());
    }
}


