package common;

import common.exception.*;
import org.junit.jupiter.api.Test;
import server.http.Request;
import server.http.Response;

import static org.junit.jupiter.api.Assertions.*;

class ControllerAuthTest {
    static class Dummy extends Controller {
        Dummy(){ super("/x"); }
        @Override public Response handle(Request r, String s){ return null; }
    }

    @Test void require_missing_401() {
        var c = new Dummy();
        var req = new Request();
        assertThrows(CredentialMissmatchException.class, () -> c.requireAuthentication(req));
    }

    @Test void check_id_mismatch_403() {
        var c = new Dummy();
        var req = new Request(); req.setAuthUserId(5);
        assertThrows(ForbiddenException.class, () -> c.checkAuthorizationByUserId(req, 7));
    }

    @Test void check_username_ok() {
        var c = new Dummy();
        var req = new Request(); req.setAuthUsername("max");
        assertDoesNotThrow(() -> c.checkAuthorizationByUsername(req, "max"));
    }
}
