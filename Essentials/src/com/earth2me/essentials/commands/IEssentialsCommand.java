package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.Server;
import org.bukkit.command.Command;
import java.util.List;


/**
 * <p>IEssentialsCommand interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IEssentialsCommand {
    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getName();

    /**
     * <p>run.</p>
     *
     * @param server a {@link org.bukkit.Server} object.
     * @param user a {@link com.earth2me.essentials.User} object.
     * @param commandLabel a {@link java.lang.String} object.
     * @param cmd a {@link org.bukkit.command.Command} object.
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    void run(Server server, User user, String commandLabel, Command cmd, String[] args) throws Exception;

    /**
     * <p>run.</p>
     *
     * @param server a {@link org.bukkit.Server} object.
     * @param sender a {@link com.earth2me.essentials.CommandSource} object.
     * @param commandLabel a {@link java.lang.String} object.
     * @param cmd a {@link org.bukkit.command.Command} object.
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    void run(Server server, CommandSource sender, String commandLabel, Command cmd, String[] args) throws Exception;

    /**
     * <p>tabComplete.</p>
     *
     * @param server a {@link org.bukkit.Server} object.
     * @param user a {@link com.earth2me.essentials.User} object.
     * @param commandLabel a {@link java.lang.String} object.
     * @param cmd a {@link org.bukkit.command.Command} object.
     * @param args an array of {@link java.lang.String} objects.
     * @return a {@link java.util.List} object.
     */
    List<String> tabComplete(Server server, User user, String commandLabel, Command cmd, String[] args);

    /**
     * <p>tabComplete.</p>
     *
     * @param server a {@link org.bukkit.Server} object.
     * @param sender a {@link com.earth2me.essentials.CommandSource} object.
     * @param commandLabel a {@link java.lang.String} object.
     * @param cmd a {@link org.bukkit.command.Command} object.
     * @param args an array of {@link java.lang.String} objects.
     * @return a {@link java.util.List} object.
     */
    List<String> tabComplete(Server server, CommandSource sender, String commandLabel, Command cmd, String[] args);

    /**
     * <p>setEssentials.</p>
     *
     * @param ess a {@link net.ess3.api.IEssentials} object.
     */
    void setEssentials(IEssentials ess);

    /**
     * <p>setEssentialsModule.</p>
     *
     * @param module a {@link com.earth2me.essentials.IEssentialsModule} object.
     */
    void setEssentialsModule(IEssentialsModule module);
}
