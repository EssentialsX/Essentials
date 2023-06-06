package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.FakeServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EssentialsCommandNodeTest {
    private FakeServer fakeServer;
    private CommandSource playerSource;
    private CommandSource consoleSource;

    @BeforeEach
    void setup() {
        fakeServer = FakeServer.getServer();

        playerSource = mock(CommandSource.class);
        consoleSource = mock(CommandSource.class);
    }

    @Test
    void testNonTerminateThrow() {
        final EssentialsCommandNode.Root<CommandSource> rootNode = EssentialsCommandNode.root(root -> {
            root.literal("hello", hello -> {
                hello.execute(ctx -> {
                    if (ctx.args().length < 1) {
                        ctx.sender().sendMessage("hello to who?");
                    } else if (ctx.args().length < 2) {
                        ctx.sender().sendMessage("hi there " + ctx.args()[0]);
                    } else {
                        ctx.sender().sendMessage("woah hi " + String.join(" and ", ctx.args()));
                    }
                    System.out.println(Arrays.toString(ctx.args()));
                });
            });
            root.literal("bye", bye -> {
                bye.literal("forever just kidding", bye1 -> { bye1.execute(ctx -> { throw new RuntimeException("this shouldn't happen"); }); });
                bye.literal("forever", bye2 -> bye2.execute(ctx -> ctx.sender().sendMessage(":((")));

                bye.execute(ctx -> {
                    if (ctx.sender().isPlayer()) {
                        ctx.sender().sendMessage(":(");
                    } else {
                        ctx.sender().sendMessage("wait you can't leave");
                    }
                });
            });
        });

        assertThrows(NoChargeException.class, () -> rootNode.run(fakeServer, playerSource, "test", new String[]{""}), "wrongly parsed empty arg");
        assertThrows(NoChargeException.class, () -> rootNode.run(fakeServer, playerSource, "test", new String[]{"wilkommen"}), "wrongly parsed unknown literal"); // wrongly parsed German

        Executable playerHelloNoArgs = () -> rootNode.run(fakeServer, playerSource, "test", new String[]{"hello"});
        Executable playerHelloOneArg = () -> rootNode.run(fakeServer, playerSource, "test", new String[]{"hello", "world"});
        Executable playerHelloManyArgs = () -> rootNode.run(fakeServer, playerSource, "test", new String[]{"hello", "jroy", "pop", "lax", "evident"});
        Executable playerBye = () -> rootNode.run(fakeServer, playerSource, "test", new String[]{"bye", "legacy", "code"});
        Executable consoleBye = () -> rootNode.run(fakeServer, consoleSource, "test", new String[]{"bye", "player", "data"});
        Executable consoleByeForeverJk = () -> rootNode.run(fakeServer, consoleSource, "test", new String[]{"bye", "forever", "just", "kidding"});

        assertDoesNotThrow(playerHelloNoArgs, "parsing first level no-arg command");
        assertDoesNotThrow(playerHelloOneArg, "parsing first level 1 arg command");
        assertDoesNotThrow(playerHelloManyArgs, "parsing first level multi-arg command");
        assertDoesNotThrow(playerBye);
        assertDoesNotThrow(consoleBye);
        assertDoesNotThrow(consoleByeForeverJk);

        InOrder ordered = Mockito.inOrder(playerSource, consoleSource);
        ordered.verify(playerSource).sendMessage("hello to who?");
        ordered.verify(playerSource).sendMessage("hi there world");
        ordered.verify(playerSource).sendMessage("woah hi jroy and pop and lax and evident");
        ordered.verify(consoleSource).sendMessage("wait you can't leave");
        ordered.verify(consoleSource).sendMessage(":((");
    }
}