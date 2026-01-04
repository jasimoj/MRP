package common.mrp.media;

import com.sun.jdi.ClassNotPreparedException;
import common.ConnectionPool;
import common.database.Repository;
import common.exception.EntityNotFoundException;
import common.mrp.recommendation.Recommendation;
import common.mrp.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static common.database.Repository.SQL_ALREADY_EXISTS_CODE;

public class MediaRepository {
    private final ConnectionPool connectionPool;

    private static final String SELECT_BY_ID
            = "SELECT m.*,  COALESCE(AVG(r.stars), 0) AS avg_stars, COUNT(r.id) AS ratings_count FROM media m LEFT JOIN ratings r ON r.media_id = m.id WHERE m.id = ? GROUP BY m.id;";
    private static final String SELECT_ALL =
            "SELECT m.*, COALESCE(AVG(r.stars), 0) AS avg_stars, COUNT(r.id) AS ratings_count FROM media m LEFT JOIN ratings r ON r.media_id = m.id GROUP BY m.id";

    private static final String INSERT_MEDIA =
            "INSERT INTO media (title, media_type, release_year, age_restriction, description, created_by_user_id) VALUES (?, ?, ?, ?, ?, ?) RETURNING *";

    private static final String UPDATE_MEDIA =
            "UPDATE media SET title = ?, media_type = ?, release_year = ?, age_restriction = ?, description = ?, created_by_user_id = ? WHERE id = ? RETURNING *";

    private static final String DELETE_MEDIA =
            "DELETE FROM media WHERE id = ?";

    private static final String INSERT_GENRE =
            "INSERT INTO genres (name) VALUES (?) ON CONFLICT (name) DO NOTHING";

    private static final String INSERT_MEDIA_GENRE_LINK =
            "INSERT INTO media_genres (media_id, genre_id) SELECT ?, id FROM genres WHERE name = ?";

    private static final String DELETE_MEDIA_GENRES =
            "DELETE FROM media_genres WHERE media_id = ?";

    private static final String SELECT_GENRES_FOR_MEDIA =
            "SELECT g.name FROM genres g JOIN media_genres mg ON mg.genre_id = g.id WHERE mg.media_id = ? ORDER BY g.name";

    private static final String SELECT_FAVORITES_BY_USERID =
            "SELECT m.title FROM favorites f JOIN media m ON m.id = f.media_id WHERE f.user_id = ? ORDER BY m.id DESC";

    private static final String GET_TOP_GENRE_FROM_USER =
            "SELECT g.id, g.name, COUNT(*) AS cnt FROM ratings r JOIN media_genres mg ON mg.media_id = r.media_id " +
                    "JOIN genres g ON g.id = mg.genre_id WHERE r.user_id = ? AND r.stars >= 4 GROUP BY g.id, g.name ORDER BY cnt DESC LIMIT 1; ";

    private static final String GET_MEDIA_BY_GENRE = "SELECT DISTINCT m.id FROM media m JOIN media_genres mg ON mg.media_id = m.id WHERE mg.genre_id = ? " +
            "AND m.id NOT IN (SELECT media_id FROM ratings WHERE user_id = ?) LIMIT 5;";

    private static final String GET_TOP_RATINGS_FROM_USER =
            "SELECT r.media_id FROM ratings r WHERE r.user_id = ? AND r.stars >= 4 ORDER BY r.stars DESC, r.created_at DESC LIMIT 3;";

    private static final String CONTENT_SELECT =
            "SELECT m2.id, m2.title, COALESCE(AVG(r2.stars), 0) AS avg_score, COUNT(r2.id) AS ratings_count, COUNT(DISTINCT g2.id) AS shared_genres ";

    private static final String CONTENT_FROM =
            "FROM media m2 JOIN media_genres mg2 ON mg2.media_id = m2.id JOIN genres g2 ON g2.id = mg2.genre_id LEFT JOIN ratings r2 ON r2.media_id = m2.id ";

    private static final String CONTENT_WHERE_BASE =
            "WHERE m2.id NOT IN (SELECT media_id FROM ratings WHERE user_id = ?) ";

    private static final String CONTENT_EXISTS_PREFIX =
            "AND EXISTS (   SELECT 1   FROM media m1   JOIN media_genres mg1 ON mg1.media_id = m1.id   WHERE m1.id IN (%s)     AND m1.media_type = m2.media_type " +
                    " AND m2.age_restriction <= m1.age_restriction AND mg1.genre_id = mg2.genre_id) ";

    private static final String CONTENT_GROUP_ORDER =
            "GROUP BY m2.id ORDER BY shared_genres DESC, avg_score DESC, ratings_count DESC LIMIT 5;";


    private static String buildCandidates(int count) {
        String ph = String.join(", ", Collections.nCopies(count, "?"));
        return "SELECT m.id, m.title, COALESCE(AVG(r.stars), 0) AS avg_score, COUNT(r.id) AS ratings_count " +
                "FROM media m LEFT JOIN ratings r ON r.media_id = m.id WHERE m.id IN (" + ph + ") GROUP BY m.id ORDER BY avg_score DESC, ratings_count DESC LIMIT 5;";
    }

    private static String buildContentCandidates(int count) {
        String ph = String.join(", ", Collections.nCopies(count, "?"));

        return CONTENT_SELECT +
                CONTENT_FROM +
                CONTENT_WHERE_BASE +
                String.format(CONTENT_EXISTS_PREFIX, ph) +
                CONTENT_GROUP_ORDER;
    }

    public MediaRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private static Media map(ResultSet rs) throws SQLException {
        Media media = new Media();
        media.setId(rs.getInt("id"));
        media.setTitle(rs.getString("title"));
        media.setMediaType(rs.getString("media_type"));
        media.setReleaseYear(rs.getInt("release_year"));
        media.setAgeRestriction(rs.getInt("age_restriction"));
        media.setDescription(rs.getString("description"));
        media.setCreatedByUserId(rs.getInt("created_by_user_id"));
        media.setAvgStars(rs.getDouble("avg_stars"));
        media.setRatingsCount(rs.getInt("ratings_count"));

        return media;
    }

    private static Recommendation recommendationMap(ResultSet rs) throws SQLException {
        Recommendation rec = new Recommendation();
        rec.setId(rs.getInt("id"));
        rec.setTitle(rs.getString("title"));
        rec.setAvgScore(rs.getDouble("avg_score"));
        rec.setRatingsCount(rs.getInt("ratings_count"));
        return rec;
    }

    public Optional<Media> find(Integer id) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Media m = map(rs);
                    m.setGenres(loadGenres(conn, m.getId()));
                    return Optional.of(m);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("find failed", e);
        }
    }

    public List<Media> findAll() {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Media> list = new ArrayList<>();
            while (rs.next()) {
                Media m = map(rs);
                m.setGenres(loadGenres(conn, m.getId()));
                list.add(m);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
    }

    public Media save(Media media) {
        // Insert NEW Media
        if (media.getId() == 0) {
            try (Connection conn = connectionPool.getConnection()) {
                conn.setAutoCommit(false);
                try (PreparedStatement ps = conn.prepareStatement(INSERT_MEDIA)) {

                    ps.setString(1, media.getTitle());
                    ps.setString(2, media.getMediaType());
                    ps.setInt(3, media.getReleaseYear());
                    ps.setInt(4, media.getAgeRestriction());
                    ps.setString(5, media.getDescription());
                    ps.setInt(6, media.getCreatedByUserId());
                    int mediaId;
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            throw new RuntimeException("Unexpected: INSERT returned no row");
                        }
                        mediaId = rs.getInt("id");

                    }
                    saveGenre(conn, mediaId, media.getGenres());
                    conn.commit();

                    return find(mediaId).orElseThrow(() ->
                            new RuntimeException("Inserted media not found: id=" + mediaId)
                    );

                } catch (SQLException ex) {
                    conn.rollback();
                    if (SQL_ALREADY_EXISTS_CODE.equals(ex.getSQLState())) {
                        throw new RuntimeException("Media already exists");
                    }
                    throw new RuntimeException("insert media failed: " + ex.getMessage(), ex);
                } finally {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                throw new RuntimeException("insert media failed: " + e.getMessage(), e);
            }
        }

        // #################################################
        // UPDATE
        // #################################################

        try (Connection conn = connectionPool.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(UPDATE_MEDIA)) {

                ps.setString(1, media.getTitle());
                ps.setString(2, media.getMediaType());
                ps.setInt(3, media.getReleaseYear());
                ps.setInt(4, media.getAgeRestriction());
                ps.setString(5, media.getDescription());
                ps.setInt(6, media.getCreatedByUserId());
                ps.setInt(7, media.getId());
                int mediaId;

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        throw new RuntimeException("update returned no row (media not found?)");
                    }
                    mediaId = rs.getInt("id");
                }
                saveGenre(conn, mediaId, media.getGenres());
                conn.commit();

                return find(mediaId).orElseThrow(() ->
                        new RuntimeException("Updated media not found: id=" + mediaId)
                );

            } catch (SQLException ex) {
                conn.rollback();
                throw new RuntimeException("update media failed: " + ex.getMessage(), ex);
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("update media failed: " + e.getMessage(), e);
        }
    }

    public void delete(Integer id) {
        if (find(id).isEmpty()) {
            throw new EntityNotFoundException("No entity found with id: " + id);
        }

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_MEDIA)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            // Eintrag in media_genre wird durch cascade automatisch gelöscht

        } catch (SQLException e) {
            throw new RuntimeException("delete failed for id=" + id, e);
        }
    }

    public List<String> findFavoritesByUserId(int userId) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_FAVORITES_BY_USERID)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> titles = new ArrayList<>();
                while (rs.next()) {
                    titles.add(rs.getString("title"));
                }
                return titles;
            }

        } catch (SQLException e) {
            throw new RuntimeException("findFavoritesByUserId failed", e);
        }
    }

    private void saveGenre(Connection conn, int mediaId, List<String> genres) throws SQLException {
        // Alte Verbindungen löschen
        try (PreparedStatement ps = conn.prepareStatement(DELETE_MEDIA_GENRES)) {
            ps.setInt(1, mediaId);
            ps.executeUpdate();
        }

        if (genres == null) return;

        for (String genre : genres) {
            if (genre == null) continue;
            genre = genre.trim();
            if (genre.isEmpty()) continue;
            //checken ob genre schon existiert und speichern
            try (PreparedStatement psInsert = conn.prepareStatement(INSERT_GENRE)) {
                psInsert.setString(1, genre);
                psInsert.executeUpdate();
            }

            // Verlinken
            try (PreparedStatement link = conn.prepareStatement(INSERT_MEDIA_GENRE_LINK)) {
                link.setInt(1, mediaId);
                link.setString(2, genre);
                link.executeUpdate();
            }
        }
    }


    private List<String> loadGenres(Connection conn, int mediaId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_GENRES_FOR_MEDIA)) {
            ps.setInt(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> genres = new ArrayList<>();
                while (rs.next()) {
                    genres.add(rs.getString("name"));
                }
                return genres;
            }
        }
    }

    public List<Recommendation> getRecommendationByGenre(Integer userId) {
        try (Connection conn = connectionPool.getConnection()) {
            Integer topGenreId = null;
            List<Integer> mediasByGenre = new ArrayList<>();
            List<Recommendation> recommendationList = new ArrayList<>();

            //Top Genre bestimmen
            //checken welches Genre der User häufig gut bewertet (z.B. >= 4 Sterne).
            try (PreparedStatement tG = conn.prepareStatement(GET_TOP_GENRE_FROM_USER)) {
                tG.setInt(1, userId);
                try (ResultSet rs = tG.executeQuery()) {
                    if (rs.next()) {
                        topGenreId = rs.getInt("id");
                    }
                }
            }

            //Falls der User nichts bewertet hat, gibt es keine empfehlungen
            if (topGenreId == null) {
                return List.of();
            }

            // Medien von diesem genre holen die der user noch NICHT bewertet hat
            try (PreparedStatement mG = conn.prepareStatement(GET_MEDIA_BY_GENRE)) {
                mG.setInt(1, topGenreId);
                mG.setInt(2, userId);

                try (ResultSet rs = mG.executeQuery()) {
                    while (rs.next()) {
                        mediasByGenre.add(rs.getInt("id"));
                    }
                }
            }

            //Wenn keine kandidaten gefunden werden sind keine empfehlungen möglich
            if (mediasByGenre.isEmpty()) {
                return List.of();
            }

            //Kandidaten laden
            // mit builCandidates werden Ids dynamisch ins sql geladen
            String candidatesSql = buildCandidates(mediasByGenre.size());
            try (PreparedStatement mG = conn.prepareStatement(candidatesSql)) {
                for (int i = 0; i < mediasByGenre.size(); i++) {
                    mG.setInt(i + 1, mediasByGenre.get(i));
                }

                try (ResultSet rs = mG.executeQuery()) {
                    while (rs.next()) {
                        recommendationList.add(recommendationMap(rs));
                    }
                }
            }

            return recommendationList;
        } catch (SQLException e) {
            throw new RuntimeException("getRecommendationByGenre failed", e);
        }
    }

    public List<Recommendation> getRecommendationByContent(Integer userId) {
        try (Connection conn = connectionPool.getConnection()) {
            List<Integer> userTopRatingIds = new ArrayList<>();
            //Top Ratings bestimmen
            //z.B. die letzten/besten Ratings >= 4 Sterne
            try (PreparedStatement tR = conn.prepareStatement(GET_TOP_RATINGS_FROM_USER)) {
                tR.setInt(1, userId);
                try (ResultSet rs = tR.executeQuery()) {
                    while (rs.next()) {
                        userTopRatingIds.add(rs.getInt("media_id"));
                    }
                }
            }

            //Falls der User nichts bewertet hat, gibt es keine empfehlungen
            if (userTopRatingIds.isEmpty()) {
                return List.of();
            }

            // SQL dynamisch bauen:
            //buildContentCandidates erzeugt eine Query, die Medien sucht, die ähnlich zu den Top-Ratings sind
            //Genres, Mediatype, altersbeschränkung
            //und die der User noch NICHT bewertet hat.
            String sql = buildContentCandidates(userTopRatingIds.size());
            List<Recommendation> recommendations = new ArrayList<>();


            //Parameter 1 ist userId (für noch nicht bewertet)
            //danach folgen die IDs der Top-Ratings für Vergleich.
            try (PreparedStatement mG = conn.prepareStatement(sql)) {
                int index = 1;
                mG.setInt(index++, userId);

                for (Integer topRatingId : userTopRatingIds) {
                    mG.setInt(index++, topRatingId);
                }

                try (ResultSet rs = mG.executeQuery()) {
                    while (rs.next()) {
                        recommendations.add(recommendationMap(rs));
                    }
                }
            }

            return recommendations;
        } catch (SQLException e) {
            throw new RuntimeException("getRecommendationByContent failed", e);
        }
    }

    public List<Media> findByFilter(MediaFilter filter) {
        // Baut dynamisches SQL + Parameterliste (z.B. nach Titel, Genre, Jahr, Mediatype, Altersfreigabe, usw)
        SqlWithParams sqlWithParams = MediaFilterQueryBuilder.build(filter);

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlWithParams.sql)) {

            //Dynamische Paramenter der Reihe nach ins ps setzen
            for (int i = 0; i < sqlWithParams.params.size(); i++) {
                ps.setObject(i + 1, sqlWithParams.params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<Media> mediaList = new ArrayList<>();

                while (rs.next()) {
                    Media m = map(rs);
                    m.setGenres(loadGenres(conn, m.getId())); //genres seperat laden
                    mediaList.add(m);
                }
                return mediaList;

            }

        } catch (SQLException e) {
            throw new RuntimeException("findByFilter failed", e);
        }
    }

}

