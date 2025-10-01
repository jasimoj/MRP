package common.mrp.user;

import common.Controller;
import common.routing.PathUtil;
import server.http.Request;
import server.http.Response;
import server.http.Status;

public class UserController extends Controller {
    private final UserService userService;
    public UserController() {
        super("/users");
        userService = new UserService();
    }

    @Override
    public Response handle(Request request, String subPath) {

        String[] split = PathUtil.splitPath(subPath);

        if (request.getMethod().equals("GET")) {
            if (isBase(split)) {
                return ok("Ich bin ein UserController");
            }

            if (isUserProfile(split)) {
                return getUserProfile(request, PathUtil.parseId(split[0]));
            }

            if (isUserRatings(split)) {
                return getUserRatings(request, PathUtil.parseId(split[0]));
            }

            if (isUserFavorites(split)) {
                return getUserFavorites(request, PathUtil.parseId(split[0]));
            }

            if (isUserRecommendations(split)) {
                return getUserRecommendations(request, PathUtil.parseId(split[0]));
            }
        }

        if (request.getMethod().equals("PUT")) {
            // Platzhalter
            if (isUserProfile(split)) {
                return putUserProfile(request, PathUtil.parseId(split[0]));
            }
        }

        return info(Status.NOT_FOUND, "404 Not Found");

    }

    //   ----- Path Matcher ---------

    private boolean isBase(String[] split) {
        return split.length == 0;
    }

    private boolean isUserProfile(String[] split) {
        return split.length == 2 && PathUtil.isInteger(split[0]) && split[1].equals("profile");
    }

    private boolean isUserRatings(String[] split) {
        return split.length == 2 && PathUtil.isInteger(split[0]) && split[1].equals("ratings");
    }

    private boolean isUserFavorites(String[] split) {
        return split.length == 2 && PathUtil.isInteger(split[0]) && split[1].equals("favorites");
    }


    private boolean isUserRecommendations(String[] split) {
        return split.length == 2 && PathUtil.isInteger(split[0]) && split[1].equals("recommendations");
    }

    //   ------ Methoden --------
    // GET
    private Response getUserProfile(Request request, int UserId) {
        return ok(userService.getUser(UserId).getUsername());
    }

    private Response getUserRatings(Request request, int UserId) {
        return ok("Ratings from User: " + UserId);
    }

    private Response getUserFavorites(Request request, int UserId) {
        return ok("Favorites from User: " + UserId);
    }
    private Response getUserRecommendations(Request request, int UserId) {
        return ok("Get recommendations for User: " + UserId);
    }

    //PUT
    private Response putUserProfile(Request request, int UserId) {
        return ok("Added User profile: " + UserId);
    }


}
