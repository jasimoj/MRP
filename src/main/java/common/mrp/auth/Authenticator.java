package common.mrp.auth;

import java.util.Optional;

public interface Authenticator {
    Optional<AuthPrincipal> authenticate(String authorizationHeader);
}
