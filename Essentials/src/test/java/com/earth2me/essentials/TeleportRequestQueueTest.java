package com.earth2me.essentials;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TeleportRequestQueueTest {
    private ServerMock server;
    private Essentials ess;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        Essentials.TESTING = true;
        ess = MockBukkit.load(Essentials.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testRequestsAreQueuedAndRemoved() {
        final User target = ess.getUser(server.addPlayer("target"));
        final User first = ess.getUser(server.addPlayer("first"));
        final User second = ess.getUser(server.addPlayer("second"));

        assertNull(target.getNextTpaRequest(false, false, false));
        target.requestTeleport(first, false);
        target.requestTeleport(second, true);

        assertEquals(2, target.getPendingTpaKeys().size());
        assertEquals("second", target.getNextTpaRequest(false, false, false).getName());
        assertEquals("first", target.getNextTpaRequest(false, false, true).getName());
        assertTrue(target.hasOutstandingTpaRequest("second", true));

        assertNotNull(target.removeTpaRequest("second"));
        assertNull(target.getOutstandingTpaRequest("second", false));
        assertEquals("first", target.getNextTpaRequest(false, false, false).getName());
    }

    // Requesters queue onto the target from their own thread, and the target reads the queue from theirs
    @Test
    public void testQueueSurvivesConcurrentRequestsAndReads() throws Exception {
        final User target = ess.getUser(server.addPlayer("target"));
        final List<User> requesters = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            requesters.add(ess.getUser(server.addPlayer("requester" + i)));
        }

        final List<Throwable> failures = new CopyOnWriteArrayList<>();
        final AtomicBoolean writing = new AtomicBoolean(true);
        final CountDownLatch writers = new CountDownLatch(requesters.size());
        final List<Thread> threads = new ArrayList<>();
        for (final User requester : requesters) {
            threads.add(new Thread(() -> {
                try {
                    for (int i = 0; i < 3000; i++) {
                        target.requestTeleport(requester, i % 2 == 0);
                    }
                } catch (final Throwable t) {
                    failures.add(t);
                } finally {
                    writers.countDown();
                }
            }));
        }
        threads.add(new Thread(() -> {
            try {
                while (writing.get()) {
                    target.getNextTpaRequest(false, false, false);
                    target.getPendingTpaKeys();
                    target.hasOutstandingTpaRequest("requester0", false);
                    target.removeTpaRequest("requester1");
                }
            } catch (final Throwable t) {
                failures.add(t);
            }
        }));

        threads.forEach(Thread::start);
        assertTrue(writers.await(60, TimeUnit.SECONDS));
        writing.set(false);
        for (final Thread thread : threads) {
            thread.join();
        }

        assertEquals(new ArrayList<Throwable>(), failures);
    }
}
