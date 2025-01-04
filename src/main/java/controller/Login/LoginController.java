package controller.Login;

import db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController implements LoginServices{
    private static LoginController loginController;

    public static LoginController getInstance(){
        return loginController==null?loginController=new LoginController():loginController;
    }

    @Override
    public boolean authenticateUsernamePassword(String username, String password) {
        try {
            return DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM users WHERE username ='" + username + "' AND password ='" + password + "'").next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
