package com.schoolfinder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private Region region = new Region();
    private Firebase firebase = new Firebase();
    private Cloudinary cloudinary = new Cloudinary();
    private GoogleMaps googleMaps = new GoogleMaps();

    @Getter
    @Setter
    public static class Region {
        private String country = "Cameroon";
        private String currency = "XAF";
        // Centred on Yaoundé (the app lists universities in Yaoundé)
        private double mapCenterLat = 3.8480;
        private double mapCenterLng = 11.5021;
    }

    @Getter
    @Setter
    public static class Firebase {
        private boolean enabled = false;
        private String credentialsPath = "";
    }

    @Getter
    @Setter
    public static class Cloudinary {
        private String url = "";
    }

    @Getter
    @Setter
    public static class GoogleMaps {
        private String apiKey = "";
    }
}
