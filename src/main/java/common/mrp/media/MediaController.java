package common.mrp.media;

import common.Controller;
import server.http.Request;
import server.http.Response;

public class MediaController extends Controller {
    public MediaController() {super("/media");}
    @Override
    public Response handle(Request request, String subPath) {
        return ok("Media Controller");
    }
}
