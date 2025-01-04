package controller.ToDo;

import model.Todo;

import java.util.List;

public interface ToDoServices {
    boolean addTask(Todo todo);
    boolean addToDoneList(int id);
    boolean removeFromDoneList(int id, String status);
    List<Todo> getAllTodoToDO(int userId);
    List<Todo> getAllDone(int userId);

}
