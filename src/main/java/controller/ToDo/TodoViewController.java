package controller.ToDo;

import controller.Login.LoginController;
import controller.Login.LoginViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import model.Todo;
import model.User;

import java.net.URL;
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
            } else {
                showAlert(Alert.AlertType.ERROR, "Error occurred when adding ToDo.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Please fill all the fields to add a ToDo!");
        }
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

            chkPending.setOnAction(actionEvent -> handleRevertToDoStatus(todo, "Pending", taskCard));
            chkInProgress.setOnAction(actionEvent -> handleRevertToDoStatus(todo, "In Progress", taskCard));
        });
    }

    private void handleTaskCompletion(Todo todo, CheckBox chkCompleted, HBox taskCard) {
        if (chkCompleted.isSelected()) {
            if (ToDoController.getInstance().addToDoneList(todo.getId())) {
                vBoxToDO.getChildren().remove(taskCard);
                addTaskToDoneList(todo);
                refreshTaskViews();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error occurred when updating task status.");
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
        AnchorPane.setTopAnchor(chkStatus, 30.0);
        AnchorPane.setLeftAnchor(chkStatus, 10.0);
        checkboxPane.getChildren().add(chkStatus);

        if (isDone) {
            CheckBox chkInProgress = new CheckBox("In Progress");
            chkInProgress.getStyleClass().add("checkbox-status");
            AnchorPane.setTopAnchor(chkInProgress, 60.0);
            AnchorPane.setLeftAnchor(chkInProgress, 10.0);
            checkboxPane.getChildren().add(chkInProgress);
        }

        return checkboxPane;
    }
}
