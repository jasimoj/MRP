package common;

import server.http.ContentType;
import server.http.Request;
import server.http.Response;
import server.http.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router implements Application {

//    Was macht der Router?
//    Er nimmt einen Request entgegen.
//    Er entscheidet, an welchen Controller er den Request weiterleitet.
//    Er gibt am Ende wieder ein Response zurück.


    private final List<Controller> controllers = new ArrayList<>();

    public Router registerController(Controller controller) {
        controllers.add(controller);
        return this;
    }


    @Override
    public Response handle(Request request) {
        String requestPath = request.getPath();
        for (Controller controller : controllers) {
            if(matchesPrefix(requestPath, controller.getBasePath())) {
                String sub = subPath(requestPath, controller.getBasePath());
                try{
                    return controller.handle(request, sub);
                } catch (Exception e) {
                    e.printStackTrace();
                    return info(Status.INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }

        }
        return info(Status.NOT_FOUND, requestPath);
    }

    public static boolean matchesPrefix(String path, String basePath) {
        if ("/".equals(basePath)) {
            return true;
        }
        return path.equals(basePath) || path.startsWith(basePath + "/");
    }

    public static String subPath(String path, String basePath) {
        if ("/".equals(basePath)) {
            return path;
        }
        if(path.equals(basePath)) {
            return "/";
        }
        return path.substring(basePath.length());
    }

    protected Response info(Status status, String body) {
        Response r = new Response();
        r.setStatus(status);
        r.setContentType(ContentType.PLAIN_TEXT);
        r.setBody(body);
        return r;
    }
}
