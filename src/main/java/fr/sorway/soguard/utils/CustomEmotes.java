package fr.sorway.soguard.utils;

import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.Arrays;

public enum CustomEmotes {
    INFORMATION("1243524280503898153"),
    WARN("1133356050733613127"),
    WRONG("988435317134622761"),
    CORRECT("988435306598498374"),
    SPOTIFY("1243532171952525354");

    private final String id;

    CustomEmotes(String id) {
        this.id = id;
    }

    public static CustomEmotes parseEmote(String input) {
        String[] data = input.split(":");
        return Arrays.stream(values())
                .filter(customEmotes -> customEmotes.name().toLowerCase().equals(data[0]))
                .filter(customEmotes -> customEmotes.id.equals(data[1]))
                .findFirst()
                .orElse(null);
    }

    public String toDiscordEmote() {
        return String.format("<:%s:%s>", name().toLowerCase(), this.id);
    }

    public Emoji toEmoji() {
        return Emoji.fromCustom(name().toLowerCase(), Long.parseLong(this.id), false);
    }

    public String getId() {
        return id;
    }

    public String getURL() {
        return String.format("https://cdn.discordapp.com/emojis/%s.webp?size=1024&quality=lossless", this.id);
    }
}
