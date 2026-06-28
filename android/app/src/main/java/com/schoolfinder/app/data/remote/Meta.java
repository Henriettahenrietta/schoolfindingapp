package com.schoolfinder.app.data.remote;

import java.util.List;

/** Parsed by Gson from /api/v1/meta. */
public class Meta {
    public String appName;
    public String tagline;
    public String country;
    public String currency;
    public double mapCenterLat;
    public double mapCenterLng;
    public List<String> categories;
    public boolean firebaseEnabled;
}
