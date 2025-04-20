package fr.sorway.soguard.services;

import com.apptasticsoftware.rssreader.Item;
import com.apptasticsoftware.rssreader.RssReader;
import fr.sorway.soguard.data.Advisory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class VulnerabilitiesRSSReader {
    private final Logger logger = LoggerFactory.getLogger(VulnerabilitiesRSSReader.class);
    private final RssReader rssReader;

    public VulnerabilitiesRSSReader() {
        this.rssReader = new RssReader();
    }

    public List<Advisory> readAdvisories(String url) {
        final List<Advisory> advisories = new ArrayList<>();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            for (Item item : rssReader.read(url).toList()) {
                final String title = (item.getTitle().isPresent() ? item.getTitle().get() : "");
                final String description = (item.getDescription().isPresent() ? item.getDescription().get() : "");
                final String link = (item.getLink().isPresent() ? item.getLink().get() : "");
                final String publicationDate = (item.getPubDate().isPresent() ? item.getPubDate().get() : "");
                final String dateString = publicationDate.replace(": ", " ").trim();

                final Date date = dateFormat.parse(dateString);
                advisories.add(new Advisory(title, link, description, date));
            }
        } catch (Exception e) {
            logger.error("Error while reading advisories: {}", e.getMessage(), e);
        }

        return advisories;
    }
}
