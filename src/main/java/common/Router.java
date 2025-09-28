package common;

import server.http.ContentType;
import server.http.Request;
import server.http.Response;
import server.http.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router {

//    Was macht der Router?
//    Er nimmt einen Request entgegen.
//    Er entscheidet, an welchen Controller er den Request weiterleitet.
//    Er gibt am Ende wieder ein Response zurück.


    private final List<Controller> controllers = new ArrayList<>();

    public Router registerController(Controller controller) {
        controllers.add(controller);
        return this;
    }



        public Response handle(Request request) {
        String requestPath = normalize(request.getPath());
        for (Controller controller : controllers) {
            String base = normalize(controller.getBasePath());
            if (matchesPrefix(requestPath, base)) {
                String sub = subPath(requestPath, base);
                try {
                    return controller.handle(request, sub);
                } catch (Exception e) {
                    e.printStackTrace();
                    return info(Status.INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }

        }
        return info(Status.NOT_FOUND, requestPath);
    }

    private     static String normalize(String p) {
        //pfad wird vorbereitet
        if (p == null || p.isEmpty()) return "/";
        if (!p.startsWith("/")) p = "/" + p;
        if (p.length() > 1 && p.endsWith("/")) p = p.substring(0, p.length() - 1);
        return p;
    }

    // aktuell werden alle pfade die nicht richtig sind als "/" zurückgegeben
    public static boolean matchesPrefix(String path, String basePath) {
        if ("/".equals(basePath)) {
            return "/".equals(path); // für root "/"
        }
        return path.equals(basePath) || path.startsWith(basePath + "/"); // für zb "/user" und "/user/"
    }

    public static String subPath(String path, String basePath) {
        if ("/".equals(basePath)) {
            return path; // nur Root
        }
        if (path.equals(basePath)) {
            return "/"; // Basepath von Controllern
        }
        return path.substring(basePath.length()); // rest hinter basepath
    }

    protected Response info(Status status, String body) {
        Response r = new Response();
        r.setStatus(status);
        r.setContentType(ContentType.PLAIN_TEXT);

        switch (status) {
            case NOT_FOUND:
                r.setBody("404 Not Found");
                break;
            case INTERNAL_SERVER_ERROR:
                r.setBody("500 Internal Server Error");
                break;
            default:
                r.setBody(body);
        }
        return r;
    }


}

