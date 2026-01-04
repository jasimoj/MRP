package common.mrp.media;

public class MediaFilterHelper {

    public MediaFilterHelper() {
    }

    public boolean isEmptyFilter(MediaFilter f) {
        return f.title == null &&
                f.genre == null &&
                f.mediaType == null &&
                f.releaseYear == null &&
                f.ageRestriction == null &&
                f.rating == null &&
                f.sortBy == null;
    }

    public Integer intOrNull(String s) {
        try {
            return s == null ? null : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double doubleOrNull(String s) {
        try {
            return s == null ? null : Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
