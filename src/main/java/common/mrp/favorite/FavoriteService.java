package common.mrp.favorite;


import common.exception.EntityNotFoundException;
import common.exception.ForbiddenException;
import common.mrp.media.Media;
import common.mrp.media.MediaRepository;
import common.mrp.user.User;
import common.mrp.user.UserRepository;

public class FavoriteService {
    private final MediaRepository mediaRepository; // optional, für Existenz-Check
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    public FavoriteService(MediaRepository media,  UserRepository userRepository, FavoriteRepository favoriteRepository) {
        this.mediaRepository = media;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public void markAsFavorite(int mediaId, int currentUserId) {
        mediaRepository.find(mediaId).orElseThrow(EntityNotFoundException::new);
        userRepository.find(currentUserId).orElseThrow(EntityNotFoundException::new);
        favoriteRepository.save(currentUserId, mediaId);
    }

    public void unmarkAsFavorite(int mediaId, int currentUserId) {
        mediaRepository.find(mediaId).orElseThrow(EntityNotFoundException::new);
        userRepository.find(currentUserId).orElseThrow(EntityNotFoundException::new);
        favoriteRepository.delete(currentUserId, mediaId);
    }
}