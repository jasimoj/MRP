package common.mrp.auth;

import java.util.Optional;

public interface Authenticator {
    //Funktionales Interface
    //Abstraktion für Authentifizierung
    //Der Authenticator kümmert sich um die Prüfung des Authorization Headers
    //versteckt die komplette Authentifizierungslogik
    //liefert wenn gültig die geprüfte user identität
    Optional<AuthPrincipal> authenticate(String authorizationHeader);
}
