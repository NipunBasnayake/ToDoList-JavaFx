package controller.Login;

import db.DBConnection;
import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController implements LoginServices{
    private static LoginController loginController;

    public static LoginController getInstance(){
        return loginController==null?loginController=new LoginController():loginController;
    }

    @Override
    public User authenticateUsernamePassword(String username, String password) {
        try {
            ResultSet res = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM users WHERE username ='" + username + "' AND password ='" + password + "'");
            res.next();
            return new User(
                    res.getInt("user_id"),
                    res.getString("username"),
                    res.getString("password")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
