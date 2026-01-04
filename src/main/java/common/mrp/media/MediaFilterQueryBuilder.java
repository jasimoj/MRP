package common.mrp.media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MediaFilterQueryBuilder {
    // Baut dynamische SQL-Queries für Media-Suchen basierend auf einem MediaFilter.
    // Ziel: Flexible Filterung (Titel, Typ, Jahr, Genre, Bewertung, Altersfreigabe)


    private MediaFilterQueryBuilder() {
    }

    public static SqlWithParams build(MediaFilter f) {
        // Basis-Query:
        // - Media-Daten
        // - Durchschnittsbewertung (avg_stars)
        // - Anzahl der Bewertungen (ratings_count)
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT m.*, COALESCE(AVG(r.stars), 0) AS avg_stars, COUNT(r.id) AS ratings_count FROM media m LEFT JOIN ratings r ON r.media_id = m.id ");

        List<String> where = new ArrayList<>();
        List<String> having = new ArrayList<>();

        // Titel-Filter (case-insensitive Teilstring-Suche)
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

        // Altersfreigabe (Media darf höchstens diese Altersbeschränkung haben)
        if (f.ageRestriction != null) {
            where.add("m.age_restriction <= ?");
            params.add(f.ageRestriction);
        }

        // Genre-Filter:
        // EXISTS wird verwendet, um Medien zu finden, die mindestens ein bestimmtes Genre besitzen.
        if (f.genre != null && !f.genre.isBlank()) {
            where.add(" EXISTS ( SELECT 1 FROM media_genres mg JOIN genres g ON g.id = mg.genre_id WHERE mg.media_id = m.id AND LOWER(g.name) = ?)");
            params.add(f.genre.toLowerCase());
        }

        // WHERE-Klausel nur anhängen, wenn mindestens ein Filter existiert
        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", where));
        }

        // Gruppierung notwendig wegen AVG() und COUNT()
        sql.append(" GROUP BY m.id");

        // Mindestbewertung (Aggregat → HAVING)
        if (f.rating != null) {
            having.add("COALESCE(AVG(r.stars), 0) >= ?");
            params.add(f.rating);
        }

        // HAVING-Klausel nur anhängen, wenn nötig
        if (!having.isEmpty()) {
            sql.append(" HAVING ").append(String.join(" AND ", having));
        }

        // Sortierspalte abhängig vom Filterwert
        // Fallback ist Titel, um ungültige Eingaben abzufangen
        String orderCol = switch (safeLower(f.sortBy)) {
            case "title" -> "m.title";
            case "year" -> "m.release_year";
            case "score" -> "avg_stars";
            default -> "m.title";
        };

        sql.append(" ORDER BY ").append(orderCol).append(" ").append(" ASC");

        // Rückgabe von SQL + Parameterliste für PreparedStatement
        return new SqlWithParams(sql.toString(), params);
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

}
