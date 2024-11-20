import java.util.ArrayList;
import java.util.List;

public class TaskList {
    List<Task> tasks = new ArrayList<>();

    public void addTask(Task task){
        tasks.add(task);
    }

    public void removeTask(int indx){
        tasks.remove(indx);
    }
    public void editTask(int indx, Task task){
        tasks.set(indx, task);
    }
}
