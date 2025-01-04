package controller.ToDo;

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
import model.Todo;

import java.net.URL;
import java.util.ResourceBundle;

public class TodoViewController implements Initializable {

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
        if (txtDueDate.getValue() == null || txtTotoDesc.getText() == null || txtDueDate.getValue() == null || cmbStatus.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please fill all the fields to add a ToDo.!");
            alert.show();
        }else{
            if (ToDoController.getInstance().addTask(new Todo(
                    1,
                    txtTodoTitle.getText(),
                    txtTotoDesc.getText(),
                    cmbStatus.getValue(),
                    txtDueDate.getValue() != null ? txtDueDate.getValue().toString() : ""
            ))) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Todo Added Successfully.!");
                alert.show();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error occurred when add ToDo.!");
                alert.show();
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> statusList = FXCollections.observableArrayList("Pending", "In Progress");
        cmbStatus.setItems(statusList);

        refreshTaskViews();
    }

    private void refreshTaskViews() {
        vBoxToDO.getChildren().clear();
        vBoxDone.getChildren().clear();
        allToDoTasksToView();
        allDoneTasksToView();
    }

    private void allToDoTasksToView() {
        ToDoController.getInstance().getAllTodoToDO().forEach(todo -> {
            HBox taskCard = createTaskCard(todo, false);
            vBoxToDO.getChildren().add(taskCard);
            CheckBox chkCompleted = (CheckBox) ((AnchorPane) taskCard.getChildren().get(1)).getChildren().get(0);
            chkCompleted.setOnAction(actionEvent -> handleTaskCompletion(todo, chkCompleted, taskCard));
        });
    }

    private void allDoneTasksToView() {
        ToDoController.getInstance().getAllDone().forEach(todo -> {
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
                new Alert(Alert.AlertType.ERROR, "Error occurred when updating task status.", ButtonType.OK).show();
            }
        }
    }

    private void handleRevertToDoStatus(Todo todo, String status, HBox taskCard) {
        if (ToDoController.getInstance().removeFromDoneList(todo.getId(), status)) {
            vBoxDone.getChildren().remove(taskCard);
            addTaskToToDoList(todo);
            refreshTaskViews();
        } else {
            new Alert(Alert.AlertType.ERROR, "Error occurred when updating task status.", ButtonType.OK).show();
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

        VBox taskDetails = new VBox(15);
        taskDetails.getStyleClass().add("task-details");

        Label lblTaskTitle = new Label(todo.getToDoTitle());
        lblTaskTitle.getStyleClass().add("task-title");

        Label lblTaskDescription = new Label(todo.getToDoDescription());
        lblTaskDescription.getStyleClass().add("task-description");

        Label lblDate = new Label(todo.getToDodueDate() + " - " + todo.getToDoStatus());
        lblDate.getStyleClass().add("task-date");

        taskDetails.getChildren().addAll(lblTaskTitle, lblTaskDescription, lblDate);

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

        taskCard.getChildren().addAll(taskDetails, checkboxPane);
        VBox.setMargin(taskCard, new Insets(5, 0, 5, 0));

        return taskCard;
    }

}
