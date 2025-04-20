package fr.sorway.soguard;

import net.dv8tion.jda.api.Permission;

public class Constants {
    public static final String ADVISORIES_URL = "https://www.cisecurity.org/feed/advisories";
    public static final String ANSSI_ALERT = "https://www.cert.ssi.gouv.fr/feed/";
    public static final Permission[] DEFAULT_BOT_PERMISSIONS = new Permission[] {
            Permission.MESSAGE_SEND,
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_HISTORY
    };
}
