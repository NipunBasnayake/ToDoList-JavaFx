package controller.ToDo;

import db.DBConnection;
import model.Todo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ToDoController implements ToDoServices {
    private static ToDoController toDoController;

    public static ToDoController getInstance() {
        if (toDoController == null) {
            synchronized (ToDoController.class) {
                if (toDoController == null) {
                    toDoController = new ToDoController();
                }
            }
        }
        return toDoController;
    }

    @Override
    public boolean addTask(Todo todo) {
        String query = "INSERT INTO tasks (user_id, title, description, status, due_date) VALUES (?, ?, ?, ?,?)";
        try (PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setInt(1,todo.getUserId());
            statement.setString(2, todo.getToDoTitle());
            statement.setString(3, todo.getToDoDescription());
            statement.setString(4, todo.getToDoStatus());
            statement.setString(5, todo.getToDodueDate());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding task", e);
        }
    }

    @Override
    public boolean addToDoneList(int id) {
        if (id <= 0) return false;

        String query = "UPDATE tasks SET status = 'Completed' WHERE task_id = ?";
        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating task status to 'Completed'", e);
        }
    }

    @Override
    public boolean removeFromDoneList(int id, String status) {
        if (id <= 0 || status == null || status.isEmpty()) return false;

        String query = "UPDATE tasks SET status = ? WHERE task_id = ?";
        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating task status to " + status, e);
        }
    }

    @Override
    public List<Todo> getAllTodoToDO() {
        List<Todo> todoList = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE status='Pending' OR status='In Progress'";

        try (ResultSet res = DBConnection.getInstance().getConnection().createStatement().executeQuery(query)) {
            while (res.next()) {
                Todo todo = new Todo(
                        res.getInt(1),
                        res.getInt(2),
                        res.getString(3),
                        res.getString(4),
                        res.getString(5),
                        res.getString(6)
                );
                todoList.add(todo);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving tasks", e);
        }
        return todoList;
    }

    @Override
    public List<Todo> getAllDone() {
        List<Todo> doneList = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE status='Completed'";

        try (ResultSet res = DBConnection.getInstance().getConnection().createStatement().executeQuery(query)) {
            while (res.next()) {
                Todo done = new Todo(
                        res.getInt(1),
                        res.getInt(2),
                        res.getString(3),
                        res.getString(4),
                        res.getString(5),
                        res.getString(6)
                );
                doneList.add(done);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving completed tasks", e);
        }

        return doneList;
    }
}
