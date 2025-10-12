package common.mrp.user;

public class User {
    
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private String favoriteGenre;

    public User(){

    }

    public User(int id, String username, String email, String favoriteGenre, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.favoriteGenre = favoriteGenre;
        this.passwordHash = passwordHash;
    }

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
