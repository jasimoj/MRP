package common.mrp.rating;

import common.Controller;
import common.routing.PathUtil;
import server.http.Request;
import server.http.Response;
import server.http.Status;

public class RatingController extends Controller {
    public RatingController() {
        super("/ratings");
    }

    @Override
    public Response handle(Request request, String subPath) {

        String[] split = PathUtil.splitPath(subPath);

        if (request.getMethod().equals("GET")) {
            if (isBase(split)) {
                return ok("Ich bin ein RatingsController");
            }
        }

        if (request.getMethod().equals("PUT")) {
            // Platzhalter
            if (isUpdateRating(split)) {
                return putUpdateRating(request, PathUtil.parseId(split[0]));
            }
        }

        if (request.getMethod().equals("POST")) {
            // Platzhalter
            if (isRatingLike(split)) {
                return postRatingLike(request, PathUtil.parseId(split[0]));
            }
            if (isUserLogin(split)) {
                return postRatingConfirm(request, PathUtil.parseId(split[0]));
            }
        }

        if (request.getMethod().equals("DELETE")) {
            // Platzhalter
            if (isDeleteRating(split)) {
                return deleteRating(request, PathUtil.parseId(split[0]));
            }
        }

        return info(Status.NOT_FOUND, "404 Not Found");

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

    private boolean isUserRegister(String[] split) {
        return split.length == 1 && split[0].equals("register");
    }

    private boolean isUserLogin(String[] split) {
        return split.length == 1 && split[0].equals("login");
    }

    private boolean isDeleteRating(String[] split) {
        return split.length == 1 && PathUtil.isInteger(split[0]);
    }

    //   ------ Methoden --------
    // PUT
    private Response putUpdateRating(Request request, int ratingId) {
        return ok("Updated Rating: " + ratingId);
    }

    // POST
    private Response postRatingLike(Request request, int ratingId) {
        return ok("liked Rating with id: " + ratingId);
    }

    private Response postRatingConfirm(Request request, int ratingId) {
        return ok("Confirmed rating comment" + ratingId);
    }

    // DELETE
    private Response deleteRating(Request request, int ratingId) {
        return ok("Deleted rating with id:" + ratingId);
    }
    
}
