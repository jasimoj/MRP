package common.mrp.leaderboard;

import common.Controller;
import common.routing.PathUtil;
import server.http.Method;
import server.http.Request;
import server.http.Response;
import server.http.Status;

import java.util.List;

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
                return getLeaderboard();
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
    private Response getLeaderboard() {
        List<LeaderboardEntry> leaderboard = leaderboardService.getLeaderboard();
        return json(leaderboard, Status.OK);
    }
}
