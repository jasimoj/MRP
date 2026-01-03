package common.mrp.auth;

import common.exception.CredentialMissmatchException;
import common.exception.EntityNotFoundException;
import common.mrp.user.User;
import common.mrp.user.UserRepository;

import java.util.Objects;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;
    private static final String suffix = "-mrpToken";


    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserCredentials credentials) {
        if (credentials == null || credentials.getUsername().isEmpty() || credentials.getPassword().isEmpty()) {
            throw new CredentialMissmatchException("Missing username or password");
        }

        User user = new User();
        user.setUsername(credentials.getUsername());
        user.setPassword(credentials.getPassword());
        user.setEmail(null);
        return userRepository.save(user);
    }

    public Token getUserToken(UserCredentials credentials) {
        if (credentials == null || credentials.getUsername().isEmpty() || credentials.getPassword().isEmpty()) {
            throw new CredentialMissmatchException("Missing username or password");
        }

        User user = userRepository.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new CredentialMissmatchException("Invalid credentials"));

        if (!Objects.equals(user.getPassword(), credentials.getPassword())) {
            throw new CredentialMissmatchException("Invalid credentials");
        }

        return createToken(user.getUsername());
    }

    public Token createToken(String username) {
        Token token = new Token();
        token.setToken(username + suffix);
        return token;
    }

    public Optional<AuthPrincipal> verifyFromAuthorizationHeader(String authHeader) {
        String token = extractBearer(authHeader);
        if (token == null || !token.endsWith(suffix)) return Optional.empty();

        String username = token.substring(0, token.length() - suffix.length());
        if (username.isBlank()) return Optional.empty();

        return userRepository.findByUsername(username)
                .map(u -> new AuthPrincipal(u.getId(), u.getUsername()));
    }

    private String extractBearer(String auth){
        if (auth == null) return null;
        if (!auth.regionMatches(true, 0, "Bearer ", 0, 7)) return null;
        String t = auth.substring(7).trim();
        return t.isEmpty() ? null : t;
    }
}
