package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class EssentialsCommandNode<T extends CommandSource> {
    private ArrayList<EssentialsCommandNode<T>> childNodes = new ArrayList<>();

    protected EssentialsCommandNode(final Initializer<T> initializer) {
        initializer.init(new BuildContext<>(this));
    }

    protected void run(Context<T> context) throws Exception {
        for (EssentialsCommandNode<T> node : childNodes) {
            if (node.matches(context)) {
                node.run(context);
                return;
            }
        }

        // TODO: error message
        throw new NoChargeException();
    }

    protected List<String> tabComplete(Context<T> context) throws Exception {
        for (EssentialsCommandNode<T> node : childNodes) {
            if (node.matches(context)) {
                return node.tabComplete(context);
            }
        }

        // TODO: error message
        throw new NoChargeException();
    }

    public abstract boolean matches(final Context<T> context);

    public static Root<CommandSource> root(final Initializer<CommandSource> initializer) {
        return new Root<>(initializer);
    }

    public interface Initializer<T extends CommandSource> {
        void init(BuildContext<T> node);
    }

    public static class BuildContext<T extends CommandSource> {
        private final EssentialsCommandNode<T> node;

        protected BuildContext(EssentialsCommandNode<T> node) {
            this.node = node;
        }

        public void literal(final String name, final Initializer<T> initializer) {
            node.childNodes.add(new Literal<>(name, initializer));
        }

        public void execute(final Consumer<Context<T>> runHandler) {
            this.execute(runHandler, ctx -> new ArrayList<>());
        }

        public void execute(final Consumer<Context<T>> runHandler, final List<String> tabValues) {
            this.execute(runHandler, ctx -> tabValues);
        }

        public void execute(final Consumer<Context<T>> runHandler, final Function<Context<T>, List<String>> tabHandler) {
            node.childNodes.add(new Execute<>(runHandler, tabHandler));
        }
    }

    public static class Root<T extends CommandSource> extends EssentialsCommandNode<T> {
        protected Root(Initializer<T> initializer) {
            super(initializer);
        }

        @Override
        public boolean matches(Context<T> context) {
            throw new IllegalStateException("Root commands should not be placed in the tree");
        }

        public void run(Server server, T sender, String commandLabel, String[] args) throws Exception {
            run(new Context<>(server, sender, commandLabel, args));
        }

        public List<String> tabComplete(Server server, T sender, String commandLabel, String[] args) throws Exception {
            return tabComplete(new Context<>(server, sender, commandLabel, args));
        }

        // run( ... args ...)
        // tabComplete( ... args ...)
    }

    public static class Literal<T extends CommandSource> extends EssentialsCommandNode<T> {
        private final String name;

        protected Literal(String name, Initializer<T> initializer) {
            super(initializer);
            this.name = name;
        }

        public boolean matches(Context<T> context) {
            return context.args.length > 0 && context.args[0].equalsIgnoreCase(name);
        }

        @Override
        protected void run(Context<T> context) throws Exception {
            // consume argument
            context = context.next();
            super.run(context);
        }

        @Override
        protected List<String> tabComplete(Context<T> context) throws Exception {
            // consume argument
            context = context.next();
            return super.tabComplete(context);
        }
    }

    public static class Execute<T extends CommandSource> extends EssentialsCommandNode<T> {
        private final Consumer<Context<T>> runHandler;
        private final Function<Context<T>, List<String>> tabHandler;

        protected Execute(Consumer<Context<T>> runHandler, Function<Context<T>, List<String>> tabHandler) {
            super(ctx -> {});
            this.runHandler = runHandler;
            this.tabHandler = tabHandler;
        }

        @Override
        public boolean matches(Context<T> context) {
            return true;
        }

        @Override
        protected void run(Context<T> context) throws Exception {
            runHandler.accept(context);
        }

        @Override
        protected List<String> tabComplete(Context<T> context) throws Exception {
            return tabHandler.apply(context);
        }
    }

    public static class Context<T extends CommandSource> {
        private final Server server;
        private final T sender;
        private final String label;
        private final String[] args;

        protected Context(Server server, T sender, String label, String[] args) {
            this.server = server;
            this.sender = sender;
            this.label = label;
            this.args = args;
        }

        protected Context<T> next() {
            String[] nextArgs = {};
            if (this.args.length > 1) {
                nextArgs = Arrays.copyOfRange(this.args, 1, this.args.length);
            }
            return new Context<>(this.server, this.sender, this.label, nextArgs);
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
}
