package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Server;

public abstract class EssentialsBranchedCommand extends EssentialsCommand {

    private transient Map<String, Class<? extends IEssentialsCommand>> subcommands = new HashMap<>();
    private transient Map<String, String> subcommandAliases = new HashMap<>();

    protected EssentialsBranchedCommand(String name) {
        super(name);
    }

    protected void registerSubcommand(Class<? extends IEssentialsCommand> commandClass, String name, String... aliases) {
        if (subcommands.containsKey(name) || subcommandAliases.containsKey(name)) throw new RuntimeException("Can't register subcommand twice!");
        subcommands.put(name, commandClass);
        for (String alias : aliases) {
            subcommandAliases.put(alias, name);
        }
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        super.run(server, user, commandLabel, args); // TODO
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        super.run(server, sender, commandLabel, args); // TODO
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        return super.getTabCompleteOptions(server, user, commandLabel, args); // TODO
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        return super.getTabCompleteOptions(server, sender, commandLabel, args); // TODO
    }
}
