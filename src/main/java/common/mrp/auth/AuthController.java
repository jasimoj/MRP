package common.mrp.auth;

import common.Controller;
import server.http.Request;
import server.http.Response;

public class AuthController extends Controller {
    public AuthController() {super("/auth");}
    @Override
    public Response handle(Request request, String subPath) {
        return ok("Auth Controller");
    }
}
