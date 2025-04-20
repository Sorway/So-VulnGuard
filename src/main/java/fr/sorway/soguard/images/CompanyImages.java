package fr.sorway.soguard.images;

import java.util.Arrays;
import java.util.Optional;

public enum CompanyImages {
    FORTINET("https://www.ibs.com.cy/assets/mainmenu/400/images/b_hero_banner_for_fortinet.jpg"),
    CISCO("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT5uXXYjrHGNymv8AD7r5cSUvK6t54BftzilA&s"),
    MICROSOFT("https://siecledigital.fr/wp-content/uploads/2017/04/microsoft_banner1200x536.png"),
    GOOGLE("https://storage.googleapis.com/support-forums-api/attachment/thread-38753685-12277661469546851254.jpg"),
    MOZILLA("https://www.mozilla.org/media/protocol/img/logos/firefox/browser/og.4ad05d4125a5.png"),
    ARUBA("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTUg-Gba5qe1np3YbRUe-9sftgAGKsVRrJ7vJmBby2OVIDRLfoANEtc8oVkIDoOuT87yw&usqp=CAU"),
    ORACLE("https://i0.wp.com/nlindia.com/wp-content/uploads/2021/06/oracle-banner.jpg?resize=802%2C201&ssl=1");

    private final String url;

    CompanyImages(String url) {
        this.url = url;
    }

    public static Optional<CompanyImages> getImages(String name) {
        return Arrays.stream(values()).filter(companyImages -> name.toLowerCase().contains(companyImages.name().toLowerCase())).findFirst();
    }

    public String getUrl() {
        return url;
    }
}
