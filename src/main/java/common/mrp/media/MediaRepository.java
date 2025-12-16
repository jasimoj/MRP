package common.mrp.media;

import common.ConnectionPool;
import common.database.Repository;
import common.exception.EntityNotFoundException;
import common.mrp.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MediaRepository implements Repository<Media, Integer> {
    private final ConnectionPool connectionPool;

    private static final String SELECT_BY_ID
            = "SELECT * FROM media WHERE id = ?";
    private static final String SELECT_ALL
            = "SELECT * FROM media ORDER BY id";
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

        return media;
    }

    @Override
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

    @Override
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

    @Override
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

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            throw new RuntimeException("Unexpected: INSERT returned no row");
                        }

                        int mediaId = rs.getInt("id");
                        saveGenre(conn, mediaId, media.getGenres());

                        Media savedMedia = map(rs);
                        savedMedia.setGenres(loadGenres(conn, mediaId));

                        conn.commit();
                        return savedMedia;
                    }

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

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        throw new RuntimeException("update returned no row (media not found?)");
                    }

                    int mediaId = rs.getInt("id");
                    saveGenre(conn, mediaId, media.getGenres());

                    Media saved = map(rs);
                    saved.setGenres(loadGenres(conn, mediaId));

                    conn.commit();
                    return saved;
                }

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

    @Override
    public Media delete(Integer id) {
        Optional<Media> existing = find(id);
        if (existing.isEmpty()) return null;

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_MEDIA)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            return existing.get();
            // Eintrag in media_genre wird durch cascade automatisch gelöscht

        } catch (SQLException e) {
            throw new RuntimeException("delete failed for id=" + id, e);
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
            if(genre.isEmpty()) continue;
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

}

