package fr.sorway.soguard.includes;

public class HTMLParser {

    public static String parseHtmlToDiscordMessage(String htmlContent) {
        String message = htmlContent;

        message = message.replaceAll("<p[^>]*>", "")
                .replaceAll("</p>", "\n\n");

        message = message.replaceAll("<ul>", "")
                .replaceAll("</ul>", "")
                .replaceAll("<li>", "- ")
                .replaceAll("</li>", "\n");

        message = message.replaceAll("<b>(.*?)</b>", "**$1**");
        message = message.replaceAll("<strong>(.*?)</strong>", "**$1**");

        message = message.replaceAll("<span[^>]*>", "");
        message = message.replaceAll("</span>", "");

        message = message.replaceAll("<i>(.*?)</i>", "*$1*");

        return message;
    }
}
