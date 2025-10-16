package common.mrp.rating;

import common.exception.EntityNotFoundException;
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

    public Rating getRating(Integer id) {
        return ratingRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    //Für leaderboard vlt in zukunft?
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Rating updateRating(Integer id, Rating rating) {
        Rating updatedRating = ratingRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
        updatedRating.setComment(rating.getComment());
        updatedRating.setStars(rating.getStars());
        return ratingRepository.save(updatedRating);
    }

    public Rating rateMedia(Integer id, Rating rating) {
        Media media = mediaRepository.find(id).orElseThrow(EntityNotFoundException::new);
        return ratingRepository.save(rating);

    }

    public Rating likeRating(Integer id) {
        return ratingRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Rating confirmRatingComment(Integer id) {
        return ratingRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Rating deleteRating(Integer id) {
        return ratingRepository.delete(id);

    }
}
