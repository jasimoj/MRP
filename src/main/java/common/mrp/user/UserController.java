package common.mrp.user;

import common.Controller;
import common.exception.EntityNotFoundException;
import common.mrp.auth.AuthService;
import common.routing.PathUtil;
import server.http.Method;
import server.http.Request;
import server.http.Response;
import server.http.Status;

import java.util.List;


public class UserController extends Controller {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        super("/users");
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public Response handle(Request request, String subPath) {

        String[] split = PathUtil.splitPath(subPath);

        if (request.getMethod().equals(Method.GET.getValue())) {
            if (isBase(split)) {
                return text("Ich bin ein UserController");
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

        if (request.getMethod().equals(Method.PUT.getValue())) {
            if (isUserProfile(split)) {
                return putUserProfile(request, PathUtil.parseId(split[0]));
            }
        }

        if (request.getMethod().equals(Method.POST.getValue())) {
            if (isAuthRegister(split)) {
                return postAuthRegister(request);
            }
            if (isAuthToken(split)) {
                return postAuthToken(request);
            }
        }

        return text(Status.NOT_FOUND, Status.NOT_FOUND.getMessage());

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

    //AUTH
    private boolean isAuthRegister(String[] split) {
        return split.length == 1 && split[0].equals("register");
    }

    private boolean isAuthToken(String[] split) {
        return split.length == 1 && split[0].equals("token");
    }


    //   ------ Methoden --------
    // GET
    private Response getUserProfile(Request request, int userId) {
           User user =  userService.getUser(userId);
           return json(user, Status.OK);
    }

    private Response getUserRatings(Request request, int userId) {
        return text("Ratings from User: " + userId);
    }

    private Response getUserFavorites(Request request, int userId) {
        return text("Favorites from User: " + userId);
    }

    private Response getUserRecommendations(Request request, int userId) {
        return text("Get recommendations for User: " + userId);
    }

    //PUT
    private Response putUserProfile(Request request, int userId) {
        return text("Added User profile: " + userId);
    }

    //POST
    private Response postAuthRegister(Request request) {
        User user = toObject(request.getBody(), User.class);
        user = authService.registerUser(user);
        return json(user, Status.CREATED);
    }

    private Response postAuthToken(Request request) {
        return text("User logged in ");
    }
}
