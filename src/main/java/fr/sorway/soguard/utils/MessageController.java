package fr.sorway.soguard.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;
import java.time.Instant;
import java.util.function.Consumer;

public class MessageController {
    public static final Color DEFAULT_COLOR = Color.decode("#60a5fa");
    public static final Color ERROR_COLOR = Color.decode("#DD2E46");
    public static final Color SUCCESS_COLOR = Color.decode("#2ecc71");
    public static final Color INFO_COLOR = Color.decode("#546de5");
    public static final Color WARN_COLOR = Color.decode("#f5b342");

    public static void sendMessage(MessageChannelUnion channel, InteractionHook interactionHook, String message, Consumer<Message> callback) {
        if (!hasViewPermission(channel)) return;

        if (interactionHook == null) {
            channel.sendMessage(message).queue(callback);
        } else {
            interactionHook.editOriginal(message).queue(callback);
        }
    }

    public static void sendMessage(MessageChannelUnion channel, InteractionHook interactionHook, String message) {
        sendMessage(channel, interactionHook, message, null);
    }

    public static void sendEmbed(MessageChannelUnion channel, InteractionHook interactionHook, MessageEmbed embed, Consumer<Message> callback) {
        if (!hasViewPermission(channel)) return;

        if (interactionHook == null || interactionHook.isExpired()) {
            channel.sendMessageEmbeds(embed).queue(callback);
        } else {
            interactionHook.editOriginalEmbeds(embed).queue(callback);
        }
    }

    public static void sendEmbed(MessageChannelUnion channel, InteractionHook interactionHook, MessageEmbed embed) {
        sendEmbed(channel, interactionHook, embed, null);
    }

    public static void sendReaction(Message message, String unicodeEmoji) {
        if (message.getChannelType() == ChannelType.TEXT) {
            TextChannel channel = message.getChannel().asTextChannel();
            if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.VIEW_CHANNEL, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY))
                message.addReaction(Emoji.fromUnicode(unicodeEmoji)).queue();
        }
    }

    public static MessageEmbed getErrorWithMessage() {
        return getEmbed(MessageController.ERROR_COLOR, "Une erreur est survenue lors de la tentative de traitement de votre demande. Le problème a été transmis aux développeurs qui travailleront dessus rapidement.");
    }

    public static MessageEmbed getErrorWithMessage(Exception e) {
        return getEmbed(MessageController.ERROR_COLOR, String.format("Une erreur est survenue lors de la tentative de traitement de votre demande. Le problème a été transmis aux développeurs qui travailleront dessus rapidement.\n\n```%s```", e.getMessage()));
    }

    public static MessageEmbed getErrorWithMessage(String message) {
        return getEmbed(MessageController.ERROR_COLOR, String.format("Une erreur est survenue lors de la tentative de traitement de votre demande. Le problème a été transmis aux développeurs qui travailleront dessus rapidement.\n\n```%s```", message));
    }

    public static MessageEmbed getErrorWithMessage(String message, Object... args) {
        return getEmbedWithMessage(WARN_COLOR, "Attention!", CustomEmotes.WARN.getURL(), message, args);
    }

    public static MessageEmbed getSuccessWithText(String text, Object... args) {
        return getEmbedWithText(SUCCESS_COLOR, "Succès!", CustomEmotes.CORRECT.getURL(), text, args);
    }

    public static MessageEmbed getInformationWithText(String text, Object... args) {
        return getEmbedWithText(INFO_COLOR, "Information!", CustomEmotes.INFORMATION.getURL(), text, args);
    }

    public static EmbedBuilder getDefaultEmbed(JDA jda) {
        return new EmbedBuilder()
                .setFooter("So' VulnGuard", jda.getSelfUser().getAvatarUrl());
    }

    private static MessageEmbed getEmbedWithMessage(Color color, String authorText, String authorIconUrl, String message, Object... args) {
        return getEmbed(color, String.format(message, args), authorText, authorIconUrl);
    }

    private static MessageEmbed getEmbedWithText(Color color, String authorText, String authorIconUrl, String text, Object... args) {
        return getEmbed(color, String.format(text, args), authorText, authorIconUrl);
    }

    private static MessageEmbed getEmbed(Color color, String description) {
        return getEmbed(color, description, null, null);
    }

    private static MessageEmbed getEmbed(Color color, String description, String authorText, String authorIconUrl) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setDescription(description)
                .setColor(color)
                .setTimestamp(Instant.now());

        if (authorText != null && authorIconUrl != null)
            embedBuilder.setAuthor(authorText, null, authorIconUrl);

        return embedBuilder.build();
    }

    private static boolean hasViewPermission(MessageChannelUnion channel) {
        if (channel.getType().isGuild()) {
            Guild guild = ((GuildMessageChannel) channel).getGuild();
            return guild.getSelfMember().hasPermission((GuildMessageChannel) channel, Permission.VIEW_CHANNEL);
        }
        return true;
    }
}
