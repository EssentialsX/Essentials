package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.ess3.api.TranslatableException;

import java.util.List;

public class EssentialsTreeNode {
    private final String[] names;
    private final boolean hidden;
    protected transient IEssentials ess;
    protected transient IEssentialsModule module;
    protected transient EssentialsTreeCommand parent;

    public EssentialsTreeNode(final String... names) {
        this(names, false);
    }

    public EssentialsTreeNode(final String[] names, final boolean hidden) {
        this.names = names;
        this.hidden = hidden;
    }

    protected void setEssentials(final IEssentials ess) {
        this.ess = ess;
    }

    protected void setEssentialsModule(final IEssentialsModule module) {
        this.module = module;
    }

    protected void setParent(final EssentialsTreeCommand parent) {
        this.parent = parent;
    }

    public String[] names() {
        return names;
    }

    public boolean hidden() {
        return hidden;
    }

    protected void run(final User user, final String commandLabel, final String[] args) throws Exception {
        run(user.getSource(), commandLabel, args);
    }

    protected void run(final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        throw new TranslatableException("onlyPlayers", commandLabel);
    }

    protected List<String> tabComplete(final User user, final String commandLabel, final String[] args) {
        return tabComplete(user.getSource(), commandLabel, args);
    }

    protected List<String> tabComplete(final CommandSource sender, final String commandLabel, final String[] args) {
        return parent.getPlayers(sender);
    }
}
