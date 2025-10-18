// src/test/java/testutil/Users.java
package testutil;

import common.mrp.user.User;

public class Users {
    public static User u(String name, String pw) {
        User x = new User();
        x.setUsername(name);
        x.setPassword(pw);
        return x;
    }
}
