package common.mrp.rating;

import common.exception.EntityNotFoundException;
import common.exception.ForbiddenException;
import common.mrp.media.Media;
import common.mrp.media.MediaRepository;
import common.mrp.media.MediaService;

import java.util.List;

public class RatingService {
    private final RatingRepository ratingRepository;
    private final MediaRepository mediaRepository;

    public RatingService(RatingRepository ratingRepository, MediaRepository mediaRepository) {
        this.ratingRepository = ratingRepository;
        this.mediaRepository = mediaRepository;
    }

    public Rating getRating(int ratingId) {
        return ratingRepository.find(ratingId)
                .orElseThrow(EntityNotFoundException::new);
    }

    //Für leaderboard vlt in zukunft?
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Rating updateRating(int ratingId, Rating rating, int currentUserId) {
        Rating updatedRating = ratingRepository.find(ratingId)
                .orElseThrow(EntityNotFoundException::new);
        if (updatedRating.getUserId() != currentUserId) throw new ForbiddenException("Not your rating");
        updatedRating.setComment(rating.getComment());
        updatedRating.setStars(rating.getStars());
        return ratingRepository.save(updatedRating);
    }

    public Rating rateMedia(int ratingId, Rating rating, int currentUserId) {
        Media media = mediaRepository.find(ratingId).orElseThrow(EntityNotFoundException::new);
        if (media.getCreatedByUserId() != currentUserId) throw new ForbiddenException("Not your media");

        return ratingRepository.save(rating);

    }

    public Rating likeRating(int ratingId, int currentUserId) {
        Rating r = ratingRepository.find(ratingId)
                .orElseThrow(EntityNotFoundException::new);
        if (r.getUserId() != currentUserId) throw new ForbiddenException("Not your rating");
        return ratingRepository.save(r);
    }

    public Rating confirmRatingComment(int ratingId, int currentUserId) {
        Rating r= ratingRepository.find(ratingId)
                .orElseThrow(EntityNotFoundException::new);
        if (r.getUserId() != currentUserId) throw new ForbiddenException("Not your rating");
        return ratingRepository.save(r);
    }

    public Rating deleteRating(int ratingId, int currentUserId) {
        Rating r = ratingRepository.find(ratingId).orElseThrow(EntityNotFoundException::new);
        if (r.getUserId() != currentUserId) throw new ForbiddenException("Not your rating");
        return ratingRepository.delete(ratingId);
    }
}
