package auth;

import common.mrp.auth.AuthService;
import common.mrp.auth.UserCredentials;
import common.mrp.user.User;
import org.junit.jupiter.api.Test;
import testutil.FakeUserRepository;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    @Test void login_ok_gives_token() {
        var repo = new FakeUserRepository();
        var u = new User(); u.setUsername("max"); u.setPassword("pw"); repo.save(u);

        var svc = new AuthService(repo);
        var tok = svc.getUserToken(new UserCredentials("max","pw"));

        assertTrue(tok.getToken().startsWith("max"));
        assertTrue(tok.getToken().endsWith("-mrpToken"));
    }

    @Test void login_wrong_password_throws() {
        var repo = new FakeUserRepository();
        var u = new User(); u.setUsername("max"); u.setPassword("pw"); repo.save(u);

        var svc = new AuthService(repo);
        assertThrows(common.exception.CredentialMissmatchException.class,
                () -> svc.getUserToken(new UserCredentials("max","nope")));
    }
}