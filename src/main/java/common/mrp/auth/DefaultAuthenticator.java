package common.mrp.auth;

import java.util.Optional;

public class DefaultAuthenticator implements Authenticator {
    private static final String SUFFIX = "-mrpToken";
    private final AuthService authService;

    public DefaultAuthenticator(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Optional<AuthPrincipal> authenticate(String header) {
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) return Optional.empty();
        String token = header.substring(7).trim();
        if (token.isEmpty() || !token.endsWith(SUFFIX)) return Optional.empty();

        String username = token.substring(0, token.length() - SUFFIX.length());
        return authService.findByUsername(username)
                .map(u -> new AuthPrincipal(u.getId(), u.getUsername()));    }
}
