package fr.sorway.soguard.services.commands;

import fr.sorway.soguard.SoVulnGuard;
import fr.sorway.soguard.services.database.DatabaseService;
import fr.sorway.soguard.utils.MessageController;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.Logger;

public class ExecutorArgs {
    private final SoVulnGuard instance;
    private final Logger logger;
    private final SlashCommandInteractionEvent event;
    private final JDA jda;
    private final User user;
    private final Member member;
    private final Guild guild;
    private final Member selfMember;
    private final MessageChannelUnion channel;
    private final InteractionHook interactionHook;
    private final DatabaseService databaseManager;

    public ExecutorArgs(SoVulnGuard instance, Logger logger, SlashCommandInteractionEvent event, InteractionHook hook) {
        this.instance = instance;
        this.logger = logger;
        this.event = event;
        this.jda = event.getJDA();
        this.user = event.getUser();
        this.member = event.getMember();
        this.guild = event.getGuild();
        this.selfMember = event.getGuild().getSelfMember();
        this.channel = event.getChannel();
        this.interactionHook = hook;
        this.databaseManager = instance.getDatabaseService();
    }

    public void sendSuccessEmbed(String key, Object... args) {
        interactionHook.sendMessageEmbeds(MessageController.getSuccessWithText(String.format(key, args))).queue();
    }

    public void sendInformationEmbed(String key, Object... args) {
        interactionHook.sendMessageEmbeds(MessageController.getInformationWithText(String.format(key, args))).queue();
    }

    public void sendWarningEmbed(String key, Object... args) {
        interactionHook.sendMessageEmbeds(MessageController.getErrorWithMessage(String.format(key, args))).queue();
    }

    public SoVulnGuard getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public SlashCommandInteractionEvent getEvent() {
        return event;
    }

    public User getUser() {
        return user;
    }

    public JDA getJDA() {
        return jda;
    }

    public Member getMember() {
        return member;
    }

    public Guild getGuild() {
        return guild;
    }

    public Member getSelfMember() {
        return selfMember;
    }

    public MessageChannelUnion getChannel() {
        return channel;
    }

    public InteractionHook getInteractionHook() {
        return interactionHook;
    }

    public DatabaseService getDatabaseService() {
        return databaseManager;
    }
}
