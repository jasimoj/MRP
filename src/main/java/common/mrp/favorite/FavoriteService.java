package common.mrp.favorite;


import common.exception.EntityNotFoundException;
import common.mrp.media.MediaRepository;

public class FavoriteService {
    private final MediaRepository media; // optional, für Existenz-Check

    public FavoriteService(MediaRepository media) {
        this.media = media;
    }

    public void markAsFavorite(Integer mediaId) {
        media.find(mediaId).orElseThrow(EntityNotFoundException::new); // optional
        //TO IMPLEMENT
        // Favorite marken.. eigene Tabelle?

    }

    public void unmarkAsFavorite(Integer mediaId) {
        media.find(mediaId).orElseThrow(EntityNotFoundException::new); // optional
        //TO IMPLEMENT
        // Favorite marken.. eigene Tabelle?

    }
}