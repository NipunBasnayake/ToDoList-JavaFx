package controller.Login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controller.ToDo.TodoViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;

public class LoginViewController {


    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXTextField txtUserName;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        User user = LoginController.getInstance().authenticateUsernamePassword(txtUserName.getText(), txtPassword.getText());
        if (user != null) {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/todo-view.fxml"));
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("ToDo");
                stage.setResizable(false);

                // Get the controller from the loaded FXML
                TodoViewController controller = loader.getController();
                controller.setUser(user);  // Set the user object

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
        try {
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/signup-view.fxml"))));
            stage.setTitle("ToDO - Sign Up");
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage) txtUserName.getScene().getWindow();
            if (currentStage != null) {
                currentStage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load the sign-up form.");
            alert.setContentText("An error occurred while opening the sign-up form.");
            alert.show();
        }
    }

}
