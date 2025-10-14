package common.mrp.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {

    private int id;
    private String username;
    private String email;
    private String favoriteGenre;
    @JsonIgnore
    private String password;

    public User() {

    }

    public User(int id, String username, String email, String favoriteGenre, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.favoriteGenre = favoriteGenre;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
