package common.echo;

import common.Controller;
import server.http.Request;
import server.http.Response;

public class EchoController extends Controller {
    public EchoController() {
        super("/");
    }

    @Override
    public Response handle(Request request, String subPath) {
        return ok("Ich bin ein echo Controller");
    }
}
