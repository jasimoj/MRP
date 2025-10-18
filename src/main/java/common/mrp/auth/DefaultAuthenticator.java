package common.mrp.auth;

import java.util.Optional;

public class DefaultAuthenticator implements Authenticator {
    private final AuthService authService;

    public DefaultAuthenticator(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Optional<AuthPrincipal> authenticate(String header) {
        return authService.verifyFromAuthorizationHeader(header);
    }
}
