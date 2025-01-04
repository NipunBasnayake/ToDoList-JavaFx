package controller.Login;

import model.User;

public interface LoginServices {
    User authenticateUsernamePassword(String username, String password);

}
