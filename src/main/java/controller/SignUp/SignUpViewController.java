package controller.SignUp;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;

public class SignUpViewController {

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXPasswordField txtPasswordConfirm;

    @FXML
    private JFXTextField txtUserName;

    @FXML
    void btnSignUpOnAction(ActionEvent event) {
        if (txtUserName.getText().isEmpty() || txtPassword.getText().isEmpty() || txtPasswordConfirm.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Fill All Fields");
            alert.show();
        } else {
            if (txtPassword.getText().equals(txtPasswordConfirm.getText())) {
                if (SignUpController.getInstance().addUser(new User(
                        1,
                        txtUserName.getText(),
                        txtPassword.getText()
                ))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Successfully Added User");
                    alert.show();

                    Stage stage = (Stage) txtUserName.getScene().getWindow();
                    stage.close();

                    Stage stage1 = new Stage();
                    try {
                        stage1.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/login-view.fxml"))));
                        stage1.show();
                    } catch (IOException e) {
                        Alert alertError = new Alert(Alert.AlertType.ERROR);
                        alertError.setTitle("Error");
                        alertError.setHeaderText("Failed to load the login form.");
                        alertError.show();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Something Went Wrong When Adding User");
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Passwords Do Not Match");
                alert.show();
            }
        }
    }


    @FXML
    void loginOnMousePressed(MouseEvent event) {
        Stage stage1 = new Stage();
        try {
            stage1.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/login-view.fxml"))));
            stage1.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load the login form.");
            alert.show();
        }
        Stage stage = (Stage) txtUserName.getScene().getWindow();
        stage.close();
    }

}
