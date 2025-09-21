package common.mrp.rating;

import common.Controller;
import server.http.Request;
import server.http.Response;

public class RatingController extends Controller {
    public RatingController() {super("/rating");}
    @Override
    public Response handle(Request request, String subPath) {
        return ok("Ich bin ein RatingController");
    }
}
