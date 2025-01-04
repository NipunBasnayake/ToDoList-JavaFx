package controller.ToDo;

import controller.Login.LoginController;
import controller.Login.LoginViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import model.Todo;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class TodoViewController implements Initializable {

    private User user;

    @FXML
    private ComboBox<String> cmbStatus;

    @FXML
    private Label lblUserEmail;

    @FXML
    private Label lblUserName;

    @FXML
    private DatePicker txtDueDate;

    @FXML
    private TextField txtTodoTitle;

    @FXML
    private TextArea txtTotoDesc;

    @FXML
    private VBox vBoxDone;

    @FXML
    private VBox vBoxToDO;

    @FXML
    void btnAddToDoOnAction(ActionEvent event) {
        if (isInputValid()) {
            Todo todo = createTodoFromInput();
            if (ToDoController.getInstance().addTask(todo)) {
                showAlert(Alert.AlertType.INFORMATION, "Todo Added Successfully!");
                refreshTaskViews();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error occurred when adding ToDo.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Please fill all the fields to add a ToDo!");
        }
    }

    public void clearFields() {
        txtDueDate.setValue(null);
        txtTodoTitle.clear();
        txtTotoDesc.clear();
        cmbStatus.getItems().clear();
    }

    private boolean isInputValid() {
        return txtDueDate.getValue() != null &&
                !txtTotoDesc.getText().isEmpty() &&
                !txtTodoTitle.getText().isEmpty() &&
                cmbStatus.getValue() != null;
    }

    private Todo createTodoFromInput() {
        return new Todo(
                1,
                user.getUserId(),
                txtTodoTitle.getText(),
                txtTotoDesc.getText(),
                cmbStatus.getValue(),
                txtDueDate.getValue() != null ? txtDueDate.getValue().toString() : ""
        );
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public void setUser(User user) {
        this.user = user;
        lblUserName.setText(user.getUserName());
        refreshTaskViews();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (user != null) {
            lblUserName.setText(user.getUserName());
            refreshTaskViews();
        }

        ObservableList<String> statusList = FXCollections.observableArrayList("Pending", "In Progress");
        cmbStatus.setItems(statusList);
    }


    private void refreshTaskViews() {
        vBoxToDO.getChildren().clear();
        vBoxDone.getChildren().clear();
        loadTasks();
    }

    private void loadTasks() {
        loadToDoTasks();
        loadDoneTasks();
    }

    private void loadToDoTasks() {
        ToDoController.getInstance().getAllTodoToDO(user.getUserId()).forEach(todo -> {
            HBox taskCard = createTaskCard(todo, false);
            vBoxToDO.getChildren().add(taskCard);
            CheckBox chkCompleted = (CheckBox) ((AnchorPane) taskCard.getChildren().get(1)).getChildren().get(0);
            chkCompleted.setOnAction(actionEvent -> handleTaskCompletion(todo, chkCompleted, taskCard));
        });
    }

    private void loadDoneTasks() {
        ToDoController.getInstance().getAllDone(user.getUserId()).forEach(todo -> {
            HBox taskCard = createTaskCard(todo, true);
            vBoxDone.getChildren().add(taskCard);
            CheckBox chkPending = (CheckBox) ((AnchorPane) taskCard.getChildren().get(1)).getChildren().get(0);
            CheckBox chkInProgress = (CheckBox) ((AnchorPane) taskCard.getChildren().get(1)).getChildren().get(1);

            // Pending checkbox
            chkPending.setOnAction(actionEvent -> {
                Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to mark this as Pending?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = alertConfirmation.showAndWait();
                ButtonType buttonType = result.orElse(ButtonType.NO);
                if (buttonType == ButtonType.YES) {
                    handleRevertToDoStatus(todo, "Pending", taskCard);
                } else {
                    chkPending.setSelected(false);
                }
            });

            // In Progress checkbox
            chkInProgress.setOnAction(actionEvent -> {
                Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to mark this as In Progress?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = alertConfirmation.showAndWait();
                ButtonType buttonType = result.orElse(ButtonType.NO);
                if (buttonType == ButtonType.YES) {
                    handleRevertToDoStatus(todo, "In Progress", taskCard);
                } else {
                    chkInProgress.setSelected(false);
                }
            });
        });
    }


    private void handleTaskCompletion(Todo todo, CheckBox chkCompleted, HBox taskCard) {
        if (chkCompleted.isSelected()) {
            Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to mark this as completed?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alertConfirmation.showAndWait();
            ButtonType buttonType = result.orElse(ButtonType.NO);
            if (buttonType == ButtonType.YES) {
                if (ToDoController.getInstance().addToDoneList(todo.getId())) {
                    vBoxToDO.getChildren().remove(taskCard);
                    addTaskToDoneList(todo);
                    refreshTaskViews();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error occurred when updating task status.");
                }
            }
        }
    }

    private void handleRevertToDoStatus(Todo todo, String status, HBox taskCard) {
        if (ToDoController.getInstance().removeFromDoneList(todo.getId(), status)) {
            vBoxDone.getChildren().remove(taskCard);
            addTaskToToDoList(todo);
            refreshTaskViews();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error occurred when updating task status.");
        }
    }

    private void addTaskToDoneList(Todo todo) {
        HBox taskCard = createTaskCard(todo, true);
        vBoxDone.getChildren().add(taskCard);
    }

    private void addTaskToToDoList(Todo todo) {
        HBox taskCard = createTaskCard(todo, false);
        vBoxToDO.getChildren().add(taskCard);
    }

    private HBox createTaskCard(Todo todo, boolean isDone) {
        HBox taskCard = new HBox(20);
        taskCard.setPadding(new Insets(15));
        taskCard.getStyleClass().add("task-card");

        VBox taskDetails = createTaskDetails(todo);

        AnchorPane checkboxPane = createCheckboxPane(isDone);

        taskCard.getChildren().addAll(taskDetails, checkboxPane);
        VBox.setMargin(taskCard, new Insets(5, 0, 5, 0));

        return taskCard;
    }

    private VBox createTaskDetails(Todo todo) {
        VBox taskDetails = new VBox(15);
        taskDetails.getStyleClass().add("task-details");

        Label lblTaskTitle = new Label(todo.getToDoTitle());
        lblTaskTitle.getStyleClass().add("task-title");

        Label lblTaskDescription = new Label(todo.getToDoDescription());
        lblTaskDescription.getStyleClass().add("task-description");

        Label lblDate = new Label(todo.getToDodueDate() + " - " + todo.getToDoStatus());
        lblDate.getStyleClass().add("task-date");

        taskDetails.getChildren().addAll(lblTaskTitle, lblTaskDescription, lblDate);
        return taskDetails;
    }

    private AnchorPane createCheckboxPane(boolean isDone) {
        AnchorPane checkboxPane = new AnchorPane();
        checkboxPane.setPrefWidth(150);

        CheckBox chkStatus = new CheckBox(isDone ? "Pending" : "Completed");
        chkStatus.getStyleClass().add("checkbox-status");
        AnchorPane.setRightAnchor(chkStatus, 10.0);
        AnchorPane.setTopAnchor(chkStatus, 10.0);
        checkboxPane.getChildren().add(chkStatus);

        if (isDone) {
            CheckBox chkInProgress = new CheckBox("In Progress");
            chkInProgress.getStyleClass().add("checkbox-status");
            AnchorPane.setRightAnchor(chkInProgress, 10.0);
            AnchorPane.setTopAnchor(chkInProgress, 50.0);
            checkboxPane.getChildren().add(chkInProgress);
        }
        return checkboxPane;
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage loginStage = new Stage();
            loginStage.setScene(loginScene);
            loginStage.setTitle("Login");
            loginStage.setResizable(false);
            loginStage.show();

            Stage currentStage = (Stage) txtTotoDesc.getScene().getWindow();
            if (currentStage != null) {
                currentStage.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to load the login view: " + e.getMessage());
        }
    }

}
