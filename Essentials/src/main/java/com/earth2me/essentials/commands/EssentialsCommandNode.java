package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class EssentialsCommandNode<T> {
    private final ArrayList<EssentialsCommandNode<T>> childNodes = new ArrayList<>();

    protected EssentialsCommandNode(final Initializer<T> initializer) {
        initializer.init(new BuildContext<>(this));
    }

    protected void run(WalkContext<T> context) throws Exception {
        for (EssentialsCommandNode<T> node : childNodes) {
            if (node.matches(context)) {
                node.run(context);
                return;
            }
        }

        // TODO: error message
        throw new NoChargeException();
    }

    protected List<String> tabComplete(WalkContext<T> context) throws Exception {
        for (EssentialsCommandNode<T> node : childNodes) {
            if (node.matches(context)) {
                return node.tabComplete(context);
            }
        }

        // TODO: error message
        throw new NoChargeException();
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

        public void literal(final String name, final Initializer<T> initializer) {
            node.childNodes.add(new Literal<>(name, initializer));
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

        protected Literal(String name, Initializer<T> initializer) {
            super(initializer);
            this.name = name;
        }

        public boolean matches(WalkContext<T> context) {
            return context.args.length > 0 && context.args[0].equalsIgnoreCase(name);
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
