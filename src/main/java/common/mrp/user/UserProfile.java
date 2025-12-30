package common.mrp.user;

public class UserProfile {
    private int id;
    private String username;
    private int totalRatings;
    private double avgStars;
    private String favoriteGenre;
    private String email;

    public UserProfile(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public double getAvgStars() {
        return avgStars;
    }

    public void setAvgStars(double avgStars) {
        this.avgStars = avgStars;
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    public void setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
