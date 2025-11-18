package common.mrp.rating;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Rating {
    private int id;
    private int userId; //für später
    private int mediaId; //für später
    private String comment;
    private int stars;
    private int likesCount; //für später
    private boolean confirmed; //für später
    private long createdAt; //für später

    public Rating() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @JsonIgnore
    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("timestamp")
    public String getTimestampIso() {
        return java.time.Instant.ofEpochMilli(createdAt)
                .atZone(java.time.ZoneId.of("Europe/Vienna"))
                .toLocalDateTime()
                .toString();
    }
}
