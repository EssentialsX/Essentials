package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.FakeServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

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

    EssentialsCommandNode.Root<CommandSource> buildCommonTree() {
        return EssentialsCommandNode.root(root -> {
            root.literal("hello", hello -> hello.execute(ctx -> {
                if (ctx.args().length < 1) {
                    ctx.sender().sendMessage("hello to who?");
                } else if (ctx.args().length < 2) {
                    ctx.sender().sendMessage("hi there " + ctx.args()[0]);
                } else {
                    ctx.sender().sendMessage("woah hi " + String.join(" and ", ctx.args()));
                }
                System.out.println(Arrays.toString(ctx.args()));
            }));
            root.literal("bye", bye -> {
                bye.literal("forever just kidding", bye1 -> bye1.execute(ctx -> {
                    throw new RuntimeException("this shouldn't happen");
                }));
                bye.literal("forever", bye2 -> bye2.execute(ctx -> ctx.sender().sendMessage(":((")));

                bye.execute(ctx -> {
                    if (ctx.sender().isPlayer()) {
                        ctx.sender().sendMessage(":(");
                    } else {
                        ctx.sender().sendMessage("wait you can't leave");
                    }
                });
            }, "farewell", "tschuss");
        });
    }

    @Test
    void testBuild() {
        assertThrows(RuntimeException.class, () -> EssentialsCommandNode.root(root -> {}), "empty root");
        assertThrows(RuntimeException.class, () -> EssentialsCommandNode.root(root -> {
            root.literal("potato", potato -> {});
        }), "empty literal");

        assertDoesNotThrow(this::buildCommonTree, "build complete tree");
    }

    @Test
    void testEval() {
        final EssentialsCommandNode.Root<CommandSource> rootNode = buildCommonTree();

        assertThrows(NotEnoughArgumentsException.class, () -> rootNode.run(fakeServer, playerSource, "test", new String[]{""}), "wrongly parsed empty arg");
        assertThrows(NotEnoughArgumentsException.class, () -> rootNode.run(fakeServer, playerSource, "test", new String[]{"wilkommen"}), "wrongly parsed unknown literal"); // wrongly parsed German

        assertDoesNotThrow(() -> rootNode.run(fakeServer, playerSource, "test", new String[]{"hello"}), "parsing first level no-arg command");
        assertDoesNotThrow(() -> rootNode.run(fakeServer, playerSource, "test", new String[]{"hello", "world"}), "parsing first level 1 arg command");
        assertDoesNotThrow(() -> rootNode.run(fakeServer, playerSource, "test", new String[]{"hello", "jroy", "pop", "lax", "evident"}), "parsing first level multi-arg command");
        assertDoesNotThrow(() -> rootNode.run(fakeServer, playerSource, "test", new String[]{"bye", "legacy", "code"}));
        assertDoesNotThrow(() -> rootNode.run(fakeServer, consoleSource, "test", new String[]{"fAREWELL", "player", "data"}), "parsing with literal alias");
        assertDoesNotThrow(() -> rootNode.run(fakeServer, consoleSource, "test", new String[]{"bye", "forever", "just", "kidding"}));

        final InOrder ordered = inOrder(playerSource, consoleSource);
        ordered.verify(playerSource).sendMessage("hello to who?");
        ordered.verify(playerSource).sendMessage("hi there world");
        ordered.verify(playerSource).sendMessage("woah hi jroy and pop and lax and evident");
        ordered.verify(consoleSource).sendMessage("wait you can't leave");
        ordered.verify(consoleSource).sendMessage(":((");
    }
}
