package common.mrp.leaderboard;

import common.exception.EntityNotFoundException;
import common.mrp.media.Media;
import common.mrp.media.MediaRepository;
import common.mrp.rating.RatingRepository;
import common.mrp.user.User;
import common.mrp.user.UserRepository;

import java.util.List;

public class LeaderboardService {
    private final UserRepository userRepository;

    public LeaderboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<LeaderboardEntry> getLeaderboard() {
        //Gibt top 10 user zurück mit meisten ratings
        return userRepository.getLeaderboardByRatings();
    }
}
