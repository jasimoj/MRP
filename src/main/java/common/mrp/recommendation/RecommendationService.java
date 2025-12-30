package common.mrp.recommendation;

import common.mrp.media.Media;
import common.mrp.media.MediaRepository;

import java.sql.SQLException;
import java.util.List;

public class RecommendationService {
    private final MediaRepository mediaRepository;


    public RecommendationService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public List<Recommendation> getRecommendations(Integer userId, String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Missing query param 'type' (?type=genre or ?type=content)");
        }
        if(type.equals("genre")){
            return mediaRepository.getRecommendationByGenre(userId);
        }
        if(type.equals("content")){
            return mediaRepository.getRecommendationByContent(userId);
        }

        return null;
    }
}
