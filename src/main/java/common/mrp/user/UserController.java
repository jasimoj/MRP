package common.mrp.user;

import common.Controller;
import common.mrp.auth.AuthService;
import common.mrp.auth.Token;
import common.mrp.auth.UserCredentials;
import common.mrp.media.Media;
import common.mrp.rating.Rating;
import common.mrp.rating.RatingRepository;
import common.mrp.rating.RatingService;
import common.mrp.recommendation.Recommendation;
import common.mrp.recommendation.RecommendationService;
import common.routing.PathUtil;
import server.http.Method;
import server.http.Request;
import server.http.Response;
import server.http.Status;

import java.util.List;


public class UserController extends Controller {
    private final UserService userService;
    private final AuthService authService;
    private final RatingService ratingService;
    private final RecommendationService recommendationService;

    public UserController(UserService userService, AuthService authService, RatingService ratingService, RecommendationService recommendationService) {
        super("/users");
        this.userService = userService;
        this.authService = authService;
        this.ratingService = ratingService;
        this.recommendationService = recommendationService;
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
                return putUpdateUserProfile(request, PathUtil.parseId(split[0]));
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
        checkAuthorizationByUserId(request, userId);
        UserProfile user = userService.getProfile(userId);
        return json(user, Status.OK);
    }


    private Response getUserRatings(Request request, int userId) {
        checkAuthorizationByUserId(request, userId);
        userService.getUser(userId);
        List<Rating> rating = ratingService.getAllRatingsFromUser(userId);
        return json(rating, Status.OK);
    }

    private Response getUserFavorites(Request request, int userId) {
        checkAuthorizationByUserId(request, userId);
        List<String> medias =  userService.getUserFavorites(userId);
        return json(medias, Status.OK);
    }

    private Response getUserRecommendations(Request request, int userId) {
        checkAuthorizationByUserId(request, userId);
        String type = request.getQueryParam("type");
        List<Recommendation> recommendations = recommendationService.getRecommendations(userId, type);
        return json(recommendations, Status.OK);
    }

    //PUT
    private Response putUpdateUserProfile(Request request, int userId) {
        checkAuthorizationByUserId(request, userId);

        UserProfileUpdate user = toObject(request.getBody(), UserProfileUpdate.class);
        userService.updateUser(userId, user);
        return text(Status.OK, "Profile updated");
    }

    //POST
    //AUTH
    private Response postAuthRegister(Request request) {
        String body = readBodyAsString(request);
        UserCredentials credentials = toObject(body, UserCredentials.class);
        User u = authService.registerUser(credentials);
        return json(u, Status.CREATED);
    }

    private Response postAuthToken(Request request) {
        String body = readBodyAsString(request);
        UserCredentials credentials = toObject(body, UserCredentials.class);
        Token token = authService.getUserToken(credentials);
        return json(token, Status.OK);
    }
}
