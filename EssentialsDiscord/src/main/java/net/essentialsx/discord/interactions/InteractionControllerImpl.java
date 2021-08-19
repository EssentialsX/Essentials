package net.essentialsx.discord.interactions;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.essentialsx.api.v2.services.discord.InteractionCommand;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgument;
import net.essentialsx.api.v2.services.discord.InteractionController;
import net.essentialsx.api.v2.services.discord.InteractionEvent;
import net.essentialsx.api.v2.services.discord.InteractionException;
import net.essentialsx.discord.JDADiscordService;
import net.essentialsx.discord.util.DiscordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class InteractionControllerImpl extends ListenerAdapter implements InteractionController {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");

    private final JDADiscordService jda;

    private final Map<String, InteractionCommand> commandMap = new ConcurrentHashMap<>();
    private final Map<String, InteractionCommand> batchRegistrationQueue = new HashMap<>();
    private boolean initialBatchRegistration = false;

    public InteractionControllerImpl(JDADiscordService jda) {
        this.jda = jda;
        jda.getJda().addEventListener(this);
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getGuild() == null || event.getMember() == null || !commandMap.containsKey(event.getName())) {
            return;
        }

        final InteractionCommand command = commandMap.get(event.getName());

        if (command.isDisabled()) {
            event.reply(tl("discordErrorCommandDisabled")).setEphemeral(true).queue();
            return;
        }

        event.deferReply(command.isEphemeral()).queue(null, failure -> logger.log(Level.SEVERE, "Error while deferring Discord command", failure));

        final InteractionEvent interactionEvent = new InteractionEventImpl(event);
        if (!DiscordUtil.hasRoles(event.getMember(), jda.getSettings().getCommandSnowflakes(command.getName()))) {
            interactionEvent.reply(tl("noAccessCommand"));
            return;
        }
        jda.getPlugin().getEss().scheduleSyncDelayedTask(() -> command.onCommand(interactionEvent));
    }

    @Override
    public InteractionCommand getCommand(String name) {
        return commandMap.get(name);
    }

    public void processBatchRegistration() {
        if (!initialBatchRegistration && !batchRegistrationQueue.isEmpty()) {
            initialBatchRegistration = true;
            final List<CommandData> list = new ArrayList<>();
            for (final InteractionCommand command : batchRegistrationQueue.values()) {
                final CommandData data = new CommandData(command.getName(), command.getDescription());
                if (command.getArguments() != null) {
                    for (final InteractionCommandArgument argument : command.getArguments()) {
                        data.addOption(OptionType.valueOf(argument.getType().name()), argument.getName(), argument.getDescription(), argument.isRequired());
                    }
                }
                list.add(data);
            }

            jda.getGuild().updateCommands().addCommands(list).queue(success -> {
                for (final Command command : success) {
                    commandMap.put(command.getName(), batchRegistrationQueue.get(command.getName()));
                    batchRegistrationQueue.remove(command.getName());
                    if (jda.isDebug()) {
                        logger.info("Registered guild command " + command.getName() + " with id " + command.getId());
                    }
                }

                if (!batchRegistrationQueue.isEmpty()) {
                    logger.warning(batchRegistrationQueue.size() + " Discord commands were lost during command registration!");
                    if (jda.isDebug()) {
                        logger.warning("Lost commands: " + batchRegistrationQueue.keySet());
                    }
                    batchRegistrationQueue.clear();
                }
            }, failure -> {
                if (failure instanceof ErrorResponseException && ((ErrorResponseException) failure).getErrorResponse() == ErrorResponse.MISSING_ACCESS) {
                    logger.severe(tl("discordErrorCommand"));
                    return;
                }
                logger.log(Level.SEVERE, "Error while registering command", failure);
            });
        }
    }

    @Override
    public void registerCommand(InteractionCommand command) throws InteractionException {
        if (command.isDisabled()) {
            throw new InteractionException("The given command has been disabled!");
        }

        if (commandMap.containsKey(command.getName())) {
            throw new InteractionException("A command with that name is already registered!");
        }

        if (!initialBatchRegistration) {
            if (jda.isDebug()) {
                logger.info("Marked guild command for batch registration: " + command.getName());
            }
            batchRegistrationQueue.put(command.getName(), command);
            return;
        }

        final CommandData data = new CommandData(command.getName(), command.getDescription());
        if (command.getArguments() != null) {
            for (final InteractionCommandArgument argument : command.getArguments()) {
                data.addOption(OptionType.valueOf(argument.getType().name()), argument.getName(), argument.getDescription(), argument.isRequired());
            }
        }

        jda.getGuild().upsertCommand(data).queue(success -> {
            commandMap.put(command.getName(), command);
            if (jda.isDebug()) {
                logger.info("Registered guild command " + success.getName() + " with id " + success.getId());
            }
        }, failure -> {
            if (failure instanceof ErrorResponseException && ((ErrorResponseException) failure).getErrorResponse() == ErrorResponse.MISSING_ACCESS) {
                logger.severe(tl("discordErrorCommand"));
                return;
            }
            logger.log(Level.SEVERE, "Error while registering command", failure);
        });
    }

    public void shutdown() {
        try {
            jda.getGuild().updateCommands().complete();
        } catch (Throwable e) {
            logger.severe("Error while deleting commands: " + e.getMessage());
            if (jda.isDebug()) {
                e.printStackTrace();
            }
        }
        commandMap.clear();
    }
}
