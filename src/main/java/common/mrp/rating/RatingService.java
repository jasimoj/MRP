package common.mrp.rating;

import common.mrp.media.MediaRepository;

public class RatingService {
    private final RatingRepository ratingRepository;
    public RatingService(RatingRepository ratingRepository){
        this.ratingRepository = ratingRepository;
    }
}
