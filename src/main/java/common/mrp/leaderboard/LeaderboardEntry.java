package common.mrp.leaderboard;

public class LeaderboardEntry {
    private int userid;
    private String username;
    private int ratingsCount;

    public LeaderboardEntry(int userid, String username, int ratingsCount) {
        this.userid = userid;
        this.username = username;
        this.ratingsCount = ratingsCount;
    }
    public LeaderboardEntry(){};

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
