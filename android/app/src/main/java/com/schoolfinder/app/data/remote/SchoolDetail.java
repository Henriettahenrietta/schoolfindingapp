package com.schoolfinder.app.data.remote;

import java.util.List;

public class SchoolDetail {
    public long id;
    public String name;
    public String category;
    public String description;
    public String history;
    public String city;
    public String region;
    public String address;
    public Double latitude;
    public Double longitude;
    public Double tuitionFee;
    public String currency;
    public String website;
    public String phone;
    public String email;
    public String coverImageUrl;
    public double averageRating;
    public long ratingCount;
    public boolean favorite;
    public List<Program> programs;
    public List<SchoolImage> images;
}
