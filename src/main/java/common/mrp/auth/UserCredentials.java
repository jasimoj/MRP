// common/mrp/auth/UserCredentials.java
package common.mrp.auth;

public class UserCredentials {
    private String username;
    private String password;

    public UserCredentials() { }

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setUsername(String u) { this.username = u; }
    public void setPassword(String p) { this.password = p; }
}
