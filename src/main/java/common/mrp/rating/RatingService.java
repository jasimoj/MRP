package common.mrp.rating;

import common.exception.EntityNotFoundException;
import common.exception.ForbiddenException;
import common.mrp.media.Media;
import common.mrp.media.MediaRepository;
import common.mrp.user.UserRepository;

import java.util.List;

public class RatingService {
    private final RatingRepository ratingRepository;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository, MediaRepository mediaRepository,  UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
    }

    public Rating getRating(int ratingId) {
        return ratingRepository.find(ratingId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Rating getAllRatingsFromMedia(int mediaId) {
        return ratingRepository.find(mediaId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<Rating> getAllRatingsFromUser(int userId) {
        return ratingRepository.findAll(userId);
    }

    public Rating updateRating(int ratingId, Rating rating, int currentUserId) {
        Rating updatedRating = ratingRepository.find(ratingId)
                .orElseThrow(EntityNotFoundException::new);
        if (updatedRating.getUserId() != currentUserId) throw new ForbiddenException("Not your rating");
        updatedRating.setComment(rating.getComment());
        updatedRating.setStars(rating.getStars());
        return ratingRepository.save(updatedRating);
    }

    public Rating rateMedia(int currentUserId, int mediaId, int stars, String comment) {
        if(stars < 1 || stars > 5) throw new ForbiddenException("Invalid stars");
        Media media = mediaRepository.find(mediaId).orElseThrow(EntityNotFoundException::new);

        Rating r = new Rating();

        r.setMediaId(media.getId());
        r.setUserId(currentUserId);
        r.setStars(stars);
        r.setComment(comment);
        r.setConfirmed(false);
        r.setCreatedAt(System.currentTimeMillis());

        return ratingRepository.save(r);

    }

    public void likeRating(int ratingId, int currentUserId) {
        Rating r = ratingRepository.find(ratingId).orElseThrow(EntityNotFoundException::new);
        ratingRepository.like(ratingId, currentUserId);
    }

    public Rating confirmRatingComment(int ratingId, int currentUserId) {
        Rating r= ratingRepository.find(ratingId)
                .orElseThrow(EntityNotFoundException::new);
        if (r.getUserId() != currentUserId) throw new ForbiddenException("Not your rating");
        r.setConfirmed(true);
        return ratingRepository.save(r);
    }

    public void deleteRating(int ratingId, int currentUserId) {
        Rating r = ratingRepository.find(ratingId).orElseThrow(EntityNotFoundException::new);
        if (r.getUserId() != currentUserId) throw new ForbiddenException("Not your rating");
        ratingRepository.delete(ratingId);
    }
}
