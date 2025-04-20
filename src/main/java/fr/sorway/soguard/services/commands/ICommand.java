package fr.sorway.soguard.services.commands;

import fr.sorway.soguard.SoVulnGuard;
import fr.sorway.soguard.utils.MessageController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.concurrent.CompletableFuture;

public interface ICommand {

    default void executeAsync(ExecutorArgs args) {
        CompletableFuture.runAsync(() -> {
            execute(args);
            args.getLogger().info("\uD83D\uDDF3 ({}) Commande éxécutée {} par {}", args.getGuild().getName(), args.getEvent().getName(), args.getUser().getName());
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            args.getLogger().error("❌ An error occured while executing the command.", throwable);
            args.getInteractionHook()
                    .editOriginalEmbeds(MessageController.getErrorWithMessage())
                    .queue();
            return null;
        });
    }

    void execute(ExecutorArgs args);
    default void autoComplete(SoVulnGuard instance, CommandAutoCompleteInteractionEvent event) {}

    default OptionData[] getOptions() { return new OptionData[0]; }

    default Permission[] getBotPermissions() { return new Permission[0]; }

    default Permission[] getUserPermissions() { return new Permission[0]; }
}