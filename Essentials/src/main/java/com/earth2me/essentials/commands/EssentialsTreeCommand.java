package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

public class EssentialsTreeCommand extends EssentialsCommand {
    private final Map<String, EssentialsTreeNode> nodes = new HashMap<>();
    private final List<String> publicNodes = new ArrayList<>();

    public EssentialsTreeCommand(final String command) {
        super(command);
    }

    protected void registerNode(final EssentialsTreeNode node) {
        for (final String name : node.names()) {
            nodes.put(name, node);
            if (!node.hidden()) {
                publicNodes.add(name);
            }
        }

        node.setEssentials(ess);
        node.setEssentialsModule(module);
        node.setParent(this);
    }

    @Override
    public void setEssentials(final IEssentials ess) {
        super.setEssentials(ess);
        for (final EssentialsTreeNode node : new HashSet<>(nodes.values())) {
            node.setEssentials(ess);
        }
    }

    @Override
    public void setEssentialsModule(final IEssentialsModule module) {
        super.setEssentialsModule(module);
        for (final EssentialsTreeNode node : new HashSet<>(nodes.values())) {
            node.setEssentialsModule(module);
        }
    }

    public void runDefault(final User user, final String commandLabel) throws Exception {
        runDefault(user.getSource(), commandLabel);
    }

    public void runDefault(final CommandSource sender, final String commandLabel) throws Exception {
        throw new NotEnoughArgumentsException();
    }

    @Override
    protected final void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        run(server, user.getSource(), commandLabel, args);
    }

    @Override
    protected final void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            if (sender.isPlayer() && sender.getUser() != null) {
                runDefault((User) sender.getUser(), commandLabel);
            } else {
                runDefault(sender, commandLabel);
            }
        } else {
            final EssentialsTreeNode node = nodes.get(args[0]);
            if (node != null) {
                final String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                if (sender.isPlayer() && sender.getUser() != null) {
                    node.run((User) sender.getUser(), commandLabel, newArgs);
                } else {
                    node.run(sender, commandLabel, newArgs);
                }
            } else {
                throw new NotEnoughArgumentsException();
            }
        }
    }

    @Override
    protected final List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        return getTabCompleteOptions(server, user.getSource(), commandLabel, args);
    }

    @Override
    protected final List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return publicNodes;
        }

        final EssentialsTreeNode node = nodes.get(args[0]);
        if (node != null) {
            final String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            if (sender.isPlayer() && sender.getUser() != null) {
                return node.tabComplete((User) sender.getUser(), commandLabel, newArgs);
            } else {
                return node.tabComplete(sender, commandLabel, newArgs);
            }
        }

        return Collections.emptyList();
    }
}
