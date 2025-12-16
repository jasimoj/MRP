package common.mrp.media;

import common.Controller;
import common.mrp.auth.AuthService;
import common.mrp.favorite.FavoriteService;
import common.mrp.rating.Rating;
import common.mrp.rating.RatingService;
import common.routing.PathUtil;
import server.http.Method;
import server.http.Request;
import server.http.Response;
import server.http.Status;

import java.util.List;

public class MediaController extends Controller {
    private final MediaService mediaService;
    private final RatingService ratingService;
    private final FavoriteService favoriteService;
    private final AuthService authService;

    public MediaController(MediaService mediaService, RatingService ratingService, FavoriteService favoriteService, AuthService authService) {
        super("/media");
        this.mediaService = mediaService;
        this.ratingService = ratingService;
        this.favoriteService = favoriteService;
        this.authService = authService;
    }

    @Override
    public Response handle(Request request, String subPath) {
        String[] split = PathUtil.splitPath(subPath);

        if (request.getMethod().equals(Method.GET.getValue())) {
            if (isBase(split)) {
                //Platzhalter
                return getMedia();
            }

            if (isMediaWithId(split)) {
                return getMediaById(PathUtil.parseId(split[0]));
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
                return postRateMedia(request, PathUtil.parseId(split[0]));
            }

            if (isMediaRatingFavorite(split)) {
                return postMediaFavorite(request,PathUtil.parseId(split[0]));
            }

        }

        if (request.getMethod().equals(Method.DELETE.getValue())) {
            // Platzhalter

            if (isMediaRatingFavorite(split)) {
                return deleteMediaAsFavorite(request,PathUtil.parseId(split[0]));
            }

            if (isMediaWithId(split)) {
                return deleteMedia(request,PathUtil.parseId(split[0]));
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
    private Response getMediaById(int mediaId) {
        Media media = mediaService.getMedia(mediaId);
        return json(media, Status.OK);
    }

    private Response getMedia() {
        //Sollen nur die Entries von einer media ausgegeben werden?
        //Falls ja, ist das hier nur platzhalter
        //genauere Funktion kommt, sobald DB da ist
        List<Media> mediaList = mediaService.getAllMedia();
        return json(mediaList, Status.OK);
    }


    //POST
    private Response postCreateMedia(Request request) {
        int currentUserId = requireAuthentication(request);

        MediaInput media = toObject(request.getBody(), MediaInput.class);
        Media created = mediaService.createMedia(media, currentUserId);
        return json(created, Status.CREATED);
    }

    private Response postRateMedia(Request request, int mediaId) {
        int currentUserId = requireAuthentication(request);

        Rating rating = toObject(request.getBody(), Rating.class);
        ratingService.rateMedia(currentUserId, mediaId, rating.getStars(), rating.getComment());
        return text(Status.CREATED, "Rating created");
    }

    private Response postMediaFavorite(Request request, int mediaId) {
        //wird implementiert, sobald die DB Struktur fertig ist und ich weiß in welcher Tabelle Favorites sind
        int currentUserId = requireAuthentication(request);

        favoriteService.markAsFavorite(mediaId, currentUserId);
        return text(Status.OK, "Marked as favorite");
    }

    //PUT
    private Response putUpdateMedia(Request request, int mediaId) {
        int currentUserId = requireAuthentication(request);

        MediaInput media = toObject(request.getBody(), MediaInput.class);
        mediaService.updateMedia(mediaId, media, currentUserId);
        return text(Status.OK, "Media updated");
    }


    //DELETE
    private Response deleteMediaAsFavorite(Request request, int mediaId) {
        int currentUserId = requireAuthentication(request);

        favoriteService.unmarkAsFavorite(mediaId, currentUserId);
        return text(Status.OK, "Unmarked as favorite");
    }

    private Response deleteMedia(Request request, int mediaId) {
        int currentUserId = requireAuthentication(request);

        mediaService.deleteMedia(mediaId, currentUserId);
        return text(Status.NO_CONTENT, "Media deleted");
    }

}
