package common.mrp.auth;

import common.Controller;
import common.routing.PathUtil;
import server.http.Request;
import server.http.Response;
import server.http.Status;

public class AuthController extends Controller {

    public AuthController() {
        super("/auth");
    }

    @Override
    public Response handle(Request request, String subPath) {
        String[] split = PathUtil.splitPath(subPath);
        if (request.getMethod().equals("POST")) {
            // Platzhalter
            if (isAuthRegister(split)) {
                return postAuthRegister(request);
            }
            if (isAuthToken(split)) {
                return postAuthToken(request);
            }
        }

        return info(Status.NOT_FOUND, "404 Not Found");
    }

    //   ----- Path Matcher ---------
    private boolean isAuthRegister(String[] split) {
        return split.length == 1 && split[0].equals("register");
    }

    private boolean isAuthToken(String[] split) {
        return split.length == 1 && split[0].equals("token");
    }

    //   ------ Methoden --------
    // POST
    private Response postAuthRegister(Request request) {
        return ok("Registered User");
    }

    private Response postAuthToken(Request request) {
        return ok("User logged in ");
    }
}
