package fr.sorway.soguard.data;

import java.io.Serializable;
import java.util.Date;

public record Advisory(String title, String link, String description, Date pubDate) implements Serializable {

    @Override
    public String toString() {
        return "Advisory{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", pubDate='" + pubDate + '\'' +
                '}';
    }
}
