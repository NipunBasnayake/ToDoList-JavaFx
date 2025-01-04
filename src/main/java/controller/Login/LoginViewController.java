package controller.Login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginViewController {

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXTextField txtUserName;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        if (LoginController.getInstance().authenticateUsernamePassword(txtUserName.getText(), txtPassword.getText())) {
            Stage stage = new Stage();
            try {
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/view/todo-view.fxml")));
                scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("ToDo");
                stage.setResizable(false);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login");
            alert.setHeaderText(null);
            alert.setContentText("Login Unsuccessful");
            alert.show();
        }
    }

    public void signUpOnMousePressed(MouseEvent mouseEvent) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/signup-view.fxml"))));
            stage.setTitle("ToDO - Sign Up");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
