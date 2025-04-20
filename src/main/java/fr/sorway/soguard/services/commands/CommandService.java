package fr.sorway.soguard.services.commands;

import fr.sorway.soguard.Constants;
import fr.sorway.soguard.SoVulnGuard;
import fr.sorway.soguard.services.commands.annotations.EphemeralCommand;
import fr.sorway.soguard.services.commands.annotations.SlashCommand;
import fr.sorway.soguard.services.commands.annotations.SubCommand;
import fr.sorway.soguard.utils.MessageController;
import kotlin.collections.ArrayDeque;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

public class CommandService {
    private final Logger logger = LoggerFactory.getLogger(CommandService.class);
    private final List<ICommand> commands = new ArrayDeque<>();
    private final SoVulnGuard instance;

    public CommandService(SoVulnGuard instance) {
        this.instance = instance;
        logger.info("Initializing Commands!");

        Reflections reflections = new Reflections("fr.sorway.somusic.commands");
        Set<Class<? extends ICommand>> classes = reflections.getSubTypesOf(ICommand.class);

        for (Class<? extends ICommand> aClass : classes) {
            logger.info("Adding Command: {}", aClass.getSimpleName());
            try {
                addCommand(aClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void initSlashCommand(JDA jda) {
        CommandListUpdateAction listUpdateAction = jda.updateCommands();
        Set<String> cachedParents = new HashSet<>();

        for (ICommand command : commands) {
            if (command.getClass().isAnnotationPresent(SlashCommand.class)) {
                SlashCommand commandAnnotation = command.getClass().getAnnotation(SlashCommand.class);

                CommandData commandData = new CommandDataImpl(commandAnnotation.name(), commandAnnotation.description()).addOptions(command.getOptions());
                commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.getUserPermissions()));
                commandData.setGuildOnly(true);
                listUpdateAction.addCommands(commandData);
            } else if (command.getClass().isAnnotationPresent(SubCommand.class)) {
                SubCommand subCommand = command.getClass().getAnnotation(SubCommand.class);
                String parentName = subCommand.parent();

                if (!cachedParents.contains(parentName)) {
                    SlashCommandData slashCommandData = new CommandDataImpl(parentName, subCommand.description()).addSubcommands(getSubCommands(parentName));
                    slashCommandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.getUserPermissions()));
                    slashCommandData.setGuildOnly(true);
                    listUpdateAction.addCommands(slashCommandData);
                    cachedParents.add(parentName);
                }
            }
        }

        listUpdateAction.queue();
    }


    public void addCommand(ICommand command) {
        if (!commands.contains(command))
            commands.add(command);
    }

    public Optional<ICommand> getCommandBySlashName(String name, String subCommandName) {
        if (subCommandName == null || subCommandName.isEmpty())
            return commands.stream().filter(command -> command.getClass().isAnnotationPresent(SlashCommand.class) && command.getClass().getAnnotation(SlashCommand.class).name().equals(name)).findFirst();
        for (ICommand command : this.commands) {
            if (command.getClass().isAnnotationPresent(SubCommand.class)) {
                SubCommand subCommand = command.getClass().getAnnotation(SubCommand.class);
                if (subCommand.parent().equals(name) && subCommand.name().equals(subCommandName))
                    return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    public Collection<SubcommandData> getSubCommands(String input) {
        List<SubcommandData> subCommandData = new ArrayList<>();
        for (ICommand command : this.commands) {
            if (command.getClass().isAnnotationPresent(SubCommand.class)) {
                SubCommand subCommandAnnotation = command.getClass().getAnnotation(SubCommand.class);
                if (subCommandAnnotation.parent().equals(input))
                    subCommandData.add((new SubcommandData(subCommandAnnotation.name(), subCommandAnnotation.description())).addOptions(command.getOptions()));
            }
        }
        return subCommandData;
    }

    public void execute(SlashCommandInteractionEvent event) {
        final Guild guild = event.getGuild();
        final User user = event.getUser();
        final Member member = event.getMember();
        final Optional<ICommand> command = getCommandBySlashName(event.getName(), event.getSubcommandName());

        if (command.isEmpty() || guild == null || member == null) {
            event.replyEmbeds(MessageController.getErrorWithMessage()).queue();
            return;
        }

        List<Permission> neededPermissions = Stream.concat(Arrays.stream(Constants.DEFAULT_BOT_PERMISSIONS), Arrays.stream(command.get().getBotPermissions())).toList();
        if (!guild.getSelfMember().hasPermission(neededPermissions)) {
            event.replyEmbeds(MessageController.getErrorWithMessage("Je n'ai pas les permissions suivantes: %s.", neededPermissions)).queue();
            return;
        }

        event.deferReply().setEphemeral(command.get().getClass().isAnnotationPresent(EphemeralCommand.class)).queue(interactionHook -> {
            command.get().executeAsync(new ExecutorArgs(instance, logger, event, interactionHook));
        });
    }

    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        Optional<ICommand> command = getCommandBySlashName(event.getName(), event.getSubcommandName());
        command.ifPresent(iCommand -> iCommand.autoComplete(instance, event));
    }

    public List<ICommand> getCommands() {
        return commands;
    }
}
