package common.mrp.leaderboard;

import common.Controller;
import common.routing.PathUtil;
import server.http.Method;
import server.http.Request;
import server.http.Response;
import server.http.Status;

public class LeaderboardController extends Controller {
    private final LeaderboardService leaderboardService;
    public LeaderboardController(LeaderboardService leaderboardService) {
        super("/leaderboard");
        this.leaderboardService = leaderboardService;
    }

    @Override
    public Response handle(Request request, String subPath) {
        String[] split = PathUtil.splitPath(subPath);
        if (request.getMethod().equals(Method.GET.getValue())) {
            if (isBase(split)) {
                //Platzhalter
                return getLeaderboard(request);
            }
        }
        return status(Status.NOT_FOUND);

    }

    //   ----- Path Matcher ---------
    private boolean isBase(String[] split) {
        return split.length == 0;
    }

    //   ------ Methoden --------
    // GET
    private Response getLeaderboard(Request request) {
        leaderboardService.getLeaderboard();
        return text("Leaderboard:");
    }
}
