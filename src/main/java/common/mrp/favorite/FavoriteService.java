package common.mrp.favorite;


import common.exception.EntityNotFoundException;
import common.exception.ForbiddenException;
import common.mrp.media.Media;
import common.mrp.media.MediaRepository;

public class FavoriteService {
    private final MediaRepository media; // optional, für Existenz-Check

    public FavoriteService(MediaRepository media) {
        this.media = media;
    }

    public void markAsFavorite(int mediaId, int currentUserId) {
        media.find(mediaId).orElseThrow(EntityNotFoundException::new); // optional
        //TO IMPLEMENT
        // Favorite marken.. eigene Tabelle?

    }

    public void unmarkAsFavorite(int mediaId, int currentUserId) {
        Media m = media.find(mediaId).orElseThrow(EntityNotFoundException::new); // optional
        if(m.getCreatedByUserId() != currentUserId){
            throw new ForbiddenException();
        }
        //TO IMPLEMENT
        // Favorite marken.. eigene Tabelle?

    }
}