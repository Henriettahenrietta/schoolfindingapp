package com.schoolfinder.app.data.remote;

public class CreateReviewRequest {
    public int rating;
    public String comment;

    public CreateReviewRequest(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
