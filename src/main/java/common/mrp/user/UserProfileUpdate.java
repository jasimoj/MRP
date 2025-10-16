package common.mrp.user;

public class UserProfileUpdate {
   private String email;
   private String favoriteGenre;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    public void setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
    }

    public UserProfileUpdate() {}
}
