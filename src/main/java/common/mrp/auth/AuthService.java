package common.mrp.auth;

import common.exception.CredentialMissmatchException;
import common.exception.EntityNotFoundException;
import common.mrp.user.User;
import common.mrp.user.UserRepository;

import java.util.Objects;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

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
        user.setFavoriteGenre(null);
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

    public Token createToken(String username){
        Token token = new Token();
        token.setToken(username+"-mrpToken");
        return token;
    }
}
