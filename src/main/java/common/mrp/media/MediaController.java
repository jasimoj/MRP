package common.mrp.media;

import common.Controller;
import common.routing.PathUtil;
import server.http.Request;
import server.http.Response;
import server.http.Status;

public class MediaController extends Controller {
    public MediaController() {
        super("/media");
    }

    @Override
    public Response handle(Request request, String subPath) {
        String[] split = PathUtil.splitPath(subPath);

        if (request.getMethod().equals("GET")) {
            if (isBase(split)) {
                //Platzhalter
                return getMedia(request);
            }

            if (isMediaWithId(split)) {
                return getMediaById(request, PathUtil.parseId(split[0]));
            }
        }

        if (request.getMethod().equals("PUT")) {
            // Platzhalter
            if (isMediaWithId(split)) {
                return putUpdateMedia(request, PathUtil.parseId(split[0]));
            }
        }

        if (request.getMethod().equals("POST")) {
            // Platzhalter
            if (isBase(split)) {
                return postCreateMedia(request);
            }
            if (isMediaRating(split)) {
                return postMediaRating(request, PathUtil.parseId(split[0]));
            }

            if (isMediaRatingFavorite(split)) {
                return postMediaFavorite(request, PathUtil.parseId(split[0]));
            }

        }

        if (request.getMethod().equals("DELETE")) {
            // Platzhalter

            if (isMediaWithId(split)) {
                return deleteMedia(request, PathUtil.parseId(split[0]));
            }

            if (isMediaRatingFavorite(split)) {
                return deleteMediaFavorite(request, PathUtil.parseId(split[0]));
            }
        }

        return info(Status.NOT_FOUND, "404 Not Found");

    }

    //   ----- Path Matcher ---------

    private boolean isBase(String[] split) {
        return split.length == 0;
    }

    private boolean isMediaWithId(String[] split) {
        return split.length == 1 && PathUtil.isInteger(split[0]);
    }

    private boolean isMediaRating(String[] split) {
        return split.length == 2 && PathUtil.isInteger(split[0]) && split[1].equals("rate");
    }
    private boolean isMediaRatingFavorite(String[] split) {
        return split.length == 2 && PathUtil.isInteger(split[0]) && split[1].equals("favorite");
    }

    //   ------ Methoden --------
    // GET
    private Response getMediaById(Request request, int mediaId) {
        return ok("Media with id: " + mediaId);
    }

    private Response getMedia(Request request) {
        return ok("Media List:");
    }

    //PUT
    private Response putUpdateMedia(Request request, int UserId) {
        return ok("Added User profile: " + UserId);
    }

    //POST
    private Response postCreateMedia(Request request) {
        return ok("Created Media");
    }

    private Response postMediaRating(Request request,  int mediaId) {
        return ok("Rated Media with id: " + mediaId);
    }

    private Response postMediaFavorite(Request request,  int mediaId) {
        return ok("Marked media as favorite: " + mediaId);
    }

    //DELETE
    private Response deleteMedia(Request request, int mediaId) {
        return ok("Deleted Media: " + mediaId);
    }
    private Response deleteMediaFavorite(Request request, int mediaId) {
        return ok("Unmarked media as Favorite: " + mediaId);
    }

}
