package common.mrp.leaderboard;

import common.Controller;
import common.routing.PathUtil;
import server.http.Request;
import server.http.Response;
import server.http.Status;

public class LeaderboardController extends Controller {
    public LeaderboardController() {
        super("/leaderboard");
    }

    @Override
    public Response handle(Request request, String subPath) {
        String[] split = PathUtil.splitPath(subPath);
        if (request.getMethod().equals("GET")) {
            if (isBase(split)) {
                //Platzhalter
                return getLeaderboard(request);
            }
        }
        return info(Status.NOT_FOUND, "404 Not Found");

    }

    //   ----- Path Matcher ---------
    private boolean isBase(String[] split) {
        return split.length == 0;
    }

    //   ------ Methoden --------
    // GET
    private Response getLeaderboard(Request request) {
        return ok("Leaderboard:");
    }
}
