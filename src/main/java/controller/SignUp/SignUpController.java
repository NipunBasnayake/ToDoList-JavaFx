package controller.SignUp;

import db.DBConnection;
import model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController implements SignUpServices{
    private static SignUpController signUpController;

    public static SignUpController getInstance() {
        return signUpController==null?signUpController=new SignUpController():signUpController;
    }

    @Override
    public boolean addUser(User user) {
        try {
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement("INSERT INTO users (username, password) VALUES (?,?)");
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword());
            return statement.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
