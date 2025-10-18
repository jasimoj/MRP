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
    public FavoriteService(MediaRepository media,  UserRepository userRepository) {
        this.mediaRepository = media;
        this.userRepository = userRepository;
    }

    public void markAsFavorite(int mediaId, int currentUserId) {
        mediaRepository.find(mediaId).orElseThrow(EntityNotFoundException::new); // optional
        User u =  userRepository.find(currentUserId).orElseThrow(EntityNotFoundException::new);
        u.addFavoriteMedia(mediaId);
        userRepository.save(u);
    }

    public void unmarkAsFavorite(int mediaId, int currentUserId) {
        Media m = mediaRepository.find(mediaId).orElseThrow(EntityNotFoundException::new); // optional
        if(m.getCreatedByUserId() != currentUserId){
            throw new ForbiddenException();
        }
        User u = userRepository.find(currentUserId).orElseThrow(EntityNotFoundException::new);
        u.removeFavoriteMedia(mediaId);
    }
}