package common.mrp.auth;

import java.util.Optional;

public class DefaultAuthenticator implements Authenticator {
    //Standard Implementierung des Authenticators
    //Delegiert die Prüfung des Auth Headers and den Authservice
    private final AuthService authService;

    public DefaultAuthenticator(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Optional<AuthPrincipal> authenticate(String header) {
        return authService.verifyFromAuthorizationHeader(header);
    }
}
