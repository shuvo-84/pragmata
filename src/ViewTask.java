import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.MultipleSelectionModel;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundPosition;

public class ViewTask {
    TaskList tasklist = new TaskList();
    AddButton addTaskButton = new AddButton("Add Task");
    RemoveButton removeTaskButton = new RemoveButton("Remove Task");
    EditButton editTaskButton = new EditButton("Edit Task");
    markAsComplete markAsCompleteButton = new markAsComplete("Mark as Complete");
    ListView<String> taskListView = new ListView<>();
    TextField taskDescField = new TextField();
    DatePicker taskDatePicker = new DatePicker();
    VBox root = null;
    public Scene scene = null;

    public ViewTask() {
        taskDescField.setStyle("-fx-background-color: white;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 1px;" +
                "-fx-text-fill: black;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 10px 20px;");
        taskDescField.setPrefWidth(395);
        taskDescField.setPromptText("Make a quick task note...");
        taskDatePicker.setStyle("-fx-background-color: yellow;" +
                "-fx-text-fill: red;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 5px 20px;");
        taskDatePicker.setPrefWidth(305);
        taskDatePicker.setPromptText("Due: MM/DD/YYYY");
        taskListView.setStyle("-fx-background-color: transparent;" +
                "-fx-padding: 10px 20px;");
        taskListView.setPrefSize(50, 300);
        addTaskButton.setOnAction(e -> {
            String description = taskDescField.getText();
            LocalDate dueDate = taskDatePicker.getValue();
            boolean is_found = false;
            for (Task task : tasklist.tasks) {
                String ddescription = task.getDescription();
                LocalDate ddueDate = task.getdueDate();
                boolean is_completed = task.completed;
                if(description.equalsIgnoreCase(ddescription) && ddueDate.toString().equalsIgnoreCase(dueDate.toString()) && !is_completed)is_found = true;
            }
            if (description != null && !description.isEmpty() && dueDate != null && !is_found) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt", true))) {
                    writer.write(description + "#" + dueDate.toString() + "\n");
                    writer.flush();
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Task task = new Task(description, dueDate, false);
                tasklist.addTask(task);
                taskListView.getItems().add(task.toString());
                taskDescField.clear();
                taskDatePicker.setValue(null);
            }else if (description != null && !description.isEmpty() && dueDate != null && is_found) {
                warn("Already Exist", "You Can't Add a Duplicate Task\nAt First you Need to Complete the Previous one!");
            } else if (description == null || description.isEmpty()) {
                warn("Description Empty", "Please Add a Description");
            } else {
                warn("Due Date Missing", "Please Add a Due Date");
            }
        });
        removeTaskButton.setOnAction(e -> {
            int selectIndx = taskListView.getSelectionModel().getSelectedIndex();
            if (selectIndx >= 0) {
                tasklist.removeTask(selectIndx);
                taskListView.getItems().remove(selectIndx);

                saveTask();
            } else {
                warn("No Task Selected", "Please Select the Task You Wish to Remove");
            }
        });
        editTaskButton.setOnAction(e -> {
            int selectIndx = taskListView.getSelectionModel().getSelectedIndex();
            if (selectIndx >= 0 && !tasklist.tasks.get(selectIndx).completed) {
                Dialog<CustomInputData> dialog = new Dialog<>();
                dialog.setTitle("Edit Task");
                dialog.setHeaderText(null);
                TextField textField = new TextField();
                DatePicker datePicker = new DatePicker();

                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.addRow(0, new Label("Description:"), textField);
                grid.addRow(1, new Label("Due Date:"), datePicker);
                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        String desc = textField.getText();
                        LocalDate date = datePicker.getValue();
                        return new CustomInputData(desc, date);
                    }
                    return null;
                });

                Optional<CustomInputData> result = dialog.showAndWait();
                result.ifPresent(customInputData -> {
                    boolean is_found = false;
                    for (Task task : tasklist.tasks) {
                        String ddescription = task.getDescription();
                        LocalDate ddueDate = task.getdueDate();
                        boolean is_completed = task.completed;
                        if(customInputData.getDescription().equalsIgnoreCase(ddescription) && customInputData.getDate().toString().equalsIgnoreCase(ddueDate.toString()) && !is_completed)is_found = true;
                    }
                    if (selectIndx >= 0 && !is_found) {
                        Task task = new Task(customInputData.getDescription(), customInputData.getDate(), false);
                        tasklist.editTask(selectIndx, task);
                        taskListView.getItems().set(selectIndx, task.toString());
                    }else warn("Already Exist", "You Can't Replace a Duplicate Task\nAt First you Need to Complete the Previous one!");
                });
                saveTask();
            }else if (selectIndx >= 0)warn("Already Completed!", "As This Task Has Already been Completed, You Can't Modify It.\nYou Can Only Delete It Instead of Editing It.");
            else{
                warn("No Task Selected", "Please Select the Task You Wish to Edit");
            }
        });
        markAsCompleteButton.setOnAction(event -> {
            int selectIndx = taskListView.getSelectionModel().getSelectedIndex();
            if (selectIndx >= 0 && !tasklist.tasks.get(selectIndx).completed) {

                MultipleSelectionModel<String> selectionModel = taskListView.getSelectionModel();

                ObservableList<String> selectedItems = selectionModel.getSelectedItems();
                if (!selectedItems.isEmpty()) {
                    Task now = tasklist.tasks.get(selectIndx);
                    String des = now.getDescription();
                    LocalDate date = now.getdueDate();
                    tasklist.editTask(selectIndx, new Task(des, date, true));
                    String selectedItem = selectedItems.get(0);
                    String modifiedItem = selectedItem + "\nCompleted✓✓✓✓✓";
                    int selectedIndex = taskListView.getSelectionModel().getSelectedIndex();
                    taskListView.getItems().set(selectedIndex, modifiedItem);
                }
                saveTask();
            }
            else if(selectIndx >= 0)warn("Already completed!", "This task has already been completed!");
            else{
                warn("No Task Selected", "Please Select the Task to Mark as Complete");
            }
        });
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        Image image = new Image("bg.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        root.setBackground(background);

        HBox hbox1 = new HBox(new VBox(taskDescField, new HBox(taskDatePicker, addTaskButton)));
        hbox1.setAlignment(Pos.CENTER);
        HBox hbox2 = new HBox(removeTaskButton, editTaskButton, markAsCompleteButton);
        hbox2.setAlignment(Pos.CENTER);
        root.getChildren().addAll(hbox1, taskListView, hbox2);
        scene = new Scene(root, 400, 500);
    }

    private void saveTask() {
        try (BufferedWriter wri = new BufferedWriter(new FileWriter("tasks.txt"))) {
            wri.write("");
            wri.flush();
            wri.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt", true));
            for (Task task : tasklist.tasks) {
                String ddescription = task.getDescription();
                LocalDate ddueDate = task.getdueDate();
                boolean ok = task.completed;
                if (ok)
                    ddescription = "!" + ddescription;
                writer.write(ddescription + "#" + ddueDate.toString() + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void warn(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

class CustomInputData {
    private String desc;
    private LocalDate date;

    public CustomInputData(String desc, LocalDate date) {
        this.desc = desc;
        this.date = date;
    }

    public String getDescription() {
        return desc;
    }

    public LocalDate getDate() {
        return date;
    }
}