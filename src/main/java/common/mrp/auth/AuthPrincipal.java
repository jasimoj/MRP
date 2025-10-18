package common.mrp.auth;

public class AuthPrincipal {
    private final Integer userId;
    private final String username;

    public AuthPrincipal(Integer userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
}
