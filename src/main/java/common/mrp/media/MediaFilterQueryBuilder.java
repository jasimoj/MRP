package common.mrp.media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MediaFilterQueryBuilder {

    private MediaFilterQueryBuilder() {}

    public static SqlWithParams build(MediaFilter f) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT m.*, COALESCE(AVG(r.stars), 0) AS avg_stars, COUNT(r.id) AS ratings_count FROM media m LEFT JOIN ratings r ON r.media_id = m.id ");

        List<String> where = new ArrayList<>();
        List<String> having = new ArrayList<>();

        if (f.title != null && !f.title.isBlank()) {
            where.add("LOWER(m.title) LIKE ?");
            params.add("%" + f.title.toLowerCase() + "%");
        }

        if (f.mediaType != null && !f.mediaType.isBlank()) {
            where.add("m.media_type = ?");
            params.add(f.mediaType);
        }

        if (f.releaseYear != null) {
            where.add("m.release_year = ?");
            params.add(f.releaseYear);
        }

        if (f.ageRestriction != null) {
            where.add("m.age_restriction <= ?");
            params.add(f.ageRestriction);
        }

        if (f.genre != null && !f.genre.isBlank()) {
            where.add(" EXISTS ( SELECT 1 FROM media_genres mg JOIN genres g ON g.id = mg.genre_id WHERE mg.media_id = m.id AND LOWER(g.name) = ?)");
            params.add(f.genre.toLowerCase());
        }

        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", where));
        }

        sql.append(" GROUP BY m.id");

        if (f.rating != null) {
            having.add("COALESCE(AVG(r.stars), 0) >= ?");
            params.add(f.rating);
        }

        if (!having.isEmpty()) {
            sql.append(" HAVING ").append(String.join(" AND ", having));
        }

        String orderCol = switch (safeLower(f.sortBy)) {
            case "title" -> "m.title";
            case "year"  -> "m.release_year";
            case "score" -> "avg_stars";
            default      -> "m.title";
        };

        String dir = "ASC";
        sql.append(" ORDER BY ").append(orderCol).append(" ").append(dir);

        return new SqlWithParams(sql.toString(), params);
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

}
