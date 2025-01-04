package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Todo {
    private int id;
    private String toDoTitle;
    private String toDoDescription;
    private String toDoStatus;
    private String toDodueDate;
}
