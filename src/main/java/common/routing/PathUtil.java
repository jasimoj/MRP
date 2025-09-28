package common.routing;

public class PathUtil {
    public static String[] splitPath(String subPath) {
        if (subPath == null || subPath.equals("/")) {
            return new String[0];
        }
        String path;
        if (subPath.startsWith("/")) {
            path = subPath = subPath.substring(1);
        } else {
            path = subPath;
        }

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path.isEmpty() ? new String[0] : path.split("/");
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int parseId(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bad userId");
        }
    }
}
