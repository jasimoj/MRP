package common.mrp.media;

import common.Controller;
import common.routing.PathUtil;
import server.http.Method;
import server.http.Request;
import server.http.Response;
import server.http.Status;

import java.util.ArrayList;
import java.util.List;

public class MediaController extends Controller {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        super("/media");
        this.mediaService = mediaService;
    }

    @Override
    public Response handle(Request request, String subPath) {
        String[] split = PathUtil.splitPath(subPath);

        if (request.getMethod().equals(Method.GET.getValue())) {
            if (isBase(split)) {
                //Platzhalter
                return getMedia(request);
            }

            if (isMediaWithId(split)) {
                return getMediaById(request, PathUtil.parseId(split[0]));
            }
        }

        if (request.getMethod().equals(Method.PUT.getValue())) {
            // Platzhalter
            if (isMediaWithId(split)) {
                return putUpdateMedia(request, PathUtil.parseId(split[0]));
            }
        }

        if (request.getMethod().equals(Method.POST.getValue())) {
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

        if (request.getMethod().equals(Method.DELETE.getValue())) {
            // Platzhalter

            if (isMediaWithId(split)) {
                return deleteMedia(request, PathUtil.parseId(split[0]));
            }

            if (isMediaRatingFavorite(split)) {
                return deleteMediaFavorite(request, PathUtil.parseId(split[0]));
            }
        }

        return text(Status.NOT_FOUND, Status.NOT_FOUND.getMessage());

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
        return r("Media with id: " + mediaId);
    }

    private Response getMedia(Request request) {
        List<Media> mediaList = mediaService
        return r("Media List:");
    }

    //PUT
    private Response putUpdateMedia(Request request, int UserId) {
        return r("Added User profile: " + UserId);
    }

    //POST
    private Response postCreateMedia(Request request) {
        return r("Created Media");
    }

    private Response postMediaRating(Request request,  int mediaId) {
        return r("Rated Media with id: " + mediaId);
    }

    private Response postMediaFavorite(Request request,  int mediaId) {
        return r("Marked media as favorite: " + mediaId);
    }

    //DELETE
    private Response deleteMedia(Request request, int mediaId) {
        return r("Deleted Media: " + mediaId);
    }
    private Response deleteMediaFavorite(Request request, int mediaId) {
        return r("Unmarked media as Favorite: " + mediaId);
    }

}
