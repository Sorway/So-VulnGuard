package fr.sorway.soguard;

import net.dv8tion.jda.api.Permission;

public class Constants {
    public static final String[] FEEDS = new String[]{
            "https://www.cert.ssi.gouv.fr/feed/",
            "https://filestore.fortinet.com/fortiguard/rss/outbreakalert.xml",
            "https://www.cisecurity.org/feed/advisories"
    };
    public static final Permission[] DEFAULT_BOT_PERMISSIONS = new Permission[] {
            Permission.MESSAGE_SEND,
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_HISTORY
    };
}
