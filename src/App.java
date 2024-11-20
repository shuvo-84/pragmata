import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        ViewTask viewTask = new ViewTask();
        try (BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] parts = line.split("#");
                String description = parts[0];
                LocalDate dueDate = LocalDate.parse(parts[1]);
                boolean ok = false;
                
                if(description.charAt(0) == '!') {
                    ok = true;
                    description = description.substring(1);
                }
                Task task = new Task(description, dueDate, ok);
                viewTask.tasklist.addTask(task);
                if(!ok)viewTask.taskListView.getItems().add(task.toString());
                else  viewTask.taskListView.getItems().add(task.toString2());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle("Pragmata");
        primaryStage.setScene(viewTask.scene);
        Image icon = new Image(getClass().getResourceAsStream("icon.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
