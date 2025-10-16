package common.mrp.leaderboard;

import common.exception.EntityNotFoundException;
import common.mrp.media.Media;
import common.mrp.media.MediaRepository;
import common.mrp.user.User;

import java.util.List;

public class LeaderboardService {
    public LeaderboardService(){

    }

    public List<User> getLeaderboard(){
        // Wird später implementiert wenn bekannt ist welches repo aufgerufen werden muss
        return List.of();
    }
}
