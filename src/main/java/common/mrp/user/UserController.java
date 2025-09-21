package common.mrp.user;

import common.Controller;
import server.http.Request;
import server.http.Response;
import server.http.Status;

public class UserController extends Controller {
     public UserController(){
         super("/user");
     }

    @Override
    public Response handle(Request request, String subPath) {
         if(subPath.equals("/42")){
             return ok("ich bin ein user index");
         }
        return ok("Ich bin ein UserController");
    }
}
