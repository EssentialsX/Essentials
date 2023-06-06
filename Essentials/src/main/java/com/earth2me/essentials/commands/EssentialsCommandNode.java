package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public abstract class EssentialsCommandNode<T> {
    private final ArrayList<EssentialsCommandNode<T>> childNodes = new ArrayList<>();

    protected EssentialsCommandNode(final Initializer<T> initializer) {
        initializer.init(new BuildContext<>(this));
    }

    protected void run(WalkContext<T> context) throws Exception {
        // TODO: consider moving walk logic into non-terminal node subclass
        for (EssentialsCommandNode<T> node : childNodes) {
            if (node.matches(context)) {
                node.run(context);
                return;
            }
        }

        // we only want exact matches, so throw an error
        throw new NotEnoughArgumentsException();
    }

    protected List<String> tabComplete(WalkContext<T> context) throws Exception {
        // TODO: consider moving walk logic into non-terminal node subclass

        // try and full match first
        for (EssentialsCommandNode<T> node : childNodes) {
            if (node.matches(context)) {
                return node.tabComplete(context);
            }
        }

        // try for partial matches
        final ArrayList<String> parts = new ArrayList<>();
        if (context.args.length == 1) {
            for (EssentialsCommandNode<T> node : childNodes) {
                if (node instanceof Literal) {
                    final Literal<T> literal = (Literal<T>) node;
                    if (literal.name().startsWith(context.args[0])) {
                        parts.add(literal.name());
                    }
                }
            }
        }

        return parts;
    }

    protected List<EssentialsCommandNode<T>> getChildNodes() {
        return Collections.unmodifiableList(childNodes);
    }

    public abstract boolean matches(final WalkContext<T> context);

    public static Root<CommandSource> root(final Initializer<CommandSource> initializer) {
        return new Root<>(initializer);
    }

    public interface Initializer<T> {
        void init(BuildContext<T> node);
    }

    public static class BuildContext<T> {
        private final EssentialsCommandNode<T> node;

        protected BuildContext(EssentialsCommandNode<T> node) {
            this.node = node;
        }

        public void literal(final String name, final Initializer<T> initializer, final String... aliases) {
            node.childNodes.add(new Literal<>(name, aliases, initializer));
        }

        public void execute(final RunHandler<T> runHandler) {
            this.execute(runHandler, ctx -> new ArrayList<>());
        }

        public void execute(final RunHandler<T> runHandler, final List<String> tabValues) {
            this.execute(runHandler, ctx -> tabValues);
        }

        public void execute(final RunHandler<T> runHandler, final TabHandler<T> tabHandler) {
            node.childNodes.add(new Execute<>(runHandler, tabHandler));
        }
    }

    public static class WalkContext<T> {
        private final Server server;
        private final T sender;
        private final String label;
        private final String[] args;

        protected WalkContext(Server server, T sender, String label, String[] args) {
            this.server = server;
            this.sender = sender;
            this.label = label;
            this.args = args;
        }

        protected WalkContext<T> next() {
            String[] nextArgs = {};
            if (this.args.length > 1) {
                nextArgs = Arrays.copyOfRange(this.args, 1, this.args.length);
            }
            return new WalkContext<>(this.server, this.sender, this.label, nextArgs);
        }

        public Server server() {
            return server;
        }

        public T sender() {
            return sender;
        }

        public String label() {
            return label;
        }

        public String[] args() {
            return args;
        }
    }

    public static class Root<T> extends EssentialsCommandNode<T> {
        protected Root(Initializer<T> initializer) {
            super(initializer);
            if (getChildNodes().isEmpty()) {
                throw new IllegalStateException("Root nodes must be initialised with at least one child");
            }
        }

        @Override
        public boolean matches(WalkContext<T> context) {
            throw new IllegalStateException("Root commands should not be placed in the tree");
        }

        public void run(Server server, T sender, String commandLabel, String[] args) throws Exception {
            run(new WalkContext<>(server, sender, commandLabel, args));
        }

        public List<String> tabComplete(Server server, T sender, String commandLabel, String[] args) throws Exception {
            return tabComplete(new WalkContext<>(server, sender, commandLabel, args));
        }
    }

    public static class Literal<T> extends EssentialsCommandNode<T> {
        private final String name;
        private final HashSet<String> aliases;

        protected Literal(String name, String[] aliases, Initializer<T> initializer) {
            super(initializer);
            if (getChildNodes().isEmpty()) {
                throw new IllegalStateException("Literal nodes must be initialised with at least one child (node name: " + name + ")");
            }

            this.name = name;
            this.aliases = new HashSet<>();
            this.aliases.add(name.toLowerCase(Locale.ROOT));
            for (final String alias : aliases) {
                this.aliases.add(alias.toLowerCase(Locale.ROOT));
            }
        }

        public String name() {
            return name;
        }

        public boolean matches(WalkContext<T> context) {
            return context.args.length > 0 && aliases.contains(context.args[0].toLowerCase(Locale.ROOT));
        }

        @Override
        protected void run(WalkContext<T> context) throws Exception {
            // consume argument
            context = context.next();
            super.run(context);
        }

        @Override
        protected List<String> tabComplete(WalkContext<T> context) throws Exception {
            // consume argument
            context = context.next();
            return super.tabComplete(context);
        }
    }

    public static class Execute<T> extends EssentialsCommandNode<T> {
        private final RunHandler<T> runHandler;
        private final TabHandler<T> tabHandler;

        protected Execute(RunHandler<T> runHandler, TabHandler<T> tabHandler) {
            super(ctx -> {});
            this.runHandler = runHandler;
            this.tabHandler = tabHandler;
        }

        @Override
        public boolean matches(WalkContext<T> context) {
            return true;
        }

        @Override
        protected void run(WalkContext<T> context) throws Exception {
            runHandler.handle(context);
        }

        @Override
        protected List<String> tabComplete(WalkContext<T> context) throws Exception {
            return tabHandler.handle(context);
        }
    }

    public interface RunHandler<T> {
        void handle(WalkContext<T> ctx) throws Exception;
    }

    public interface TabHandler<T> {
        List<String> handle(WalkContext<T> ctx) throws Exception;
    }
}
