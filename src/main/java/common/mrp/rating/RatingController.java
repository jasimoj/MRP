package common.mrp.rating;

import common.Controller;
import common.mrp.auth.AuthService;
import common.routing.PathUtil;
import server.http.Method;
import server.http.Request;
import server.http.Response;
import server.http.Status;

public class RatingController extends Controller {
    private final RatingService ratingService;
    private final AuthService authService;

    public RatingController(RatingService ratingService, AuthService authService) {
        super("/ratings");
        this.ratingService = ratingService;
        this.authService = authService;

    }

    @Override
    public Response handle(Request request, String subPath) {

        String[] split = PathUtil.splitPath(subPath);

        if (request.getMethod().equals(Method.GET.getValue())) {
            if (isBase(split)) {
                return text("Ich bin ein RatingsController");
            }
        }

        if (request.getMethod().equals(Method.PUT.getValue())) {
            // Platzhalter
            if (isUpdateRating(split)) {
                return putUpdateRating(request, PathUtil.parseId(split[0]));
            }
        }

        if (request.getMethod().equals(Method.POST.getValue())) {
            // Platzhalter
            if (isRatingLike(split)) {
                return postRatingLike(request, PathUtil.parseId(split[0]));
            }
            if (isRatingConfirm(split)) {
                return postRatingConfirm(request, PathUtil.parseId(split[0]));
            }
        }

        if (request.getMethod().equals(Method.DELETE.getValue())) {
            // Platzhalter
            if (isDeleteRating(split)) {
                return deleteRating(request, PathUtil.parseId(split[0]));
            }
        }

        return text(Status.NOT_FOUND, Status.NOT_FOUND.getMessage());

    }

    //   ----- Path Matcher ---------

    private boolean isBase(String[] split) {
        return split.length == 0;
    }

    private boolean isRatingLike(String[] split) {
        return split.length == 2 && PathUtil.isInteger(split[0]) && split[1].equals("like");
    }

    private boolean isUpdateRating(String[] split) {
        return split.length == 1 && PathUtil.isInteger(split[0]);
    }

    private boolean isRatingConfirm(String[] split) {
        return split.length == 2 && PathUtil.isInteger(split[0]) && split[1].equals("confirm");
    }

    private boolean isDeleteRating(String[] split) {
        return split.length == 1 && PathUtil.isInteger(split[0]);
    }

    //   ------ Methoden --------
    // PUT
    private Response putUpdateRating(Request request, int ratingId) {
        int currentUserId = requireAuthentication(request);
        String body = readBodyAsString(request);
        Rating rating = toObject(body, Rating.class);
        ratingService.updateRating(ratingId, rating, currentUserId);
        return text(Status.OK, "Rating updated");
    }

    // POST
    private Response postRatingLike(Request request, int ratingId) {
        int currentUserId = requireAuthentication(request);

        ratingService.likeRating(ratingId, currentUserId);
        return text(Status.OK, "Rating liked");
    }

    private Response postRatingConfirm(Request request, int ratingId) {
        int currentUserId = requireAuthentication(request);

        ratingService.confirmRatingComment(ratingId, currentUserId);
        return text(Status.OK, "Comment confirmed");
    }

    // DELETE
    private Response deleteRating(Request request, int ratingId) {
        int currentUserId = requireAuthentication(request);

        ratingService.deleteRating(ratingId, currentUserId);
        return text(Status.OK, "Rating deleted");

    }

}
