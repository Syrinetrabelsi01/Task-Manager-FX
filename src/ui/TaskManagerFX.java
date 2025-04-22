/*
 This class integrates key CS202 concepts:
 - JavaFX for GUI design
 - JDBC for MySQL database interaction
 - Java Collections and Streams for filtering/sorting
 - File handling for task export
 - CSS styling
 - Event-driven programming with modular MVC structure
*/

package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import networking.DatabaseManager;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import model.Task;
import model.TaskManager;

// Entry point for the JavaFX application
public class TaskManagerFX extends Application {
    private TaskManager taskManager = new TaskManager();
    private TableView<Task> tableView = new TableView<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task Manager FX");

        //loadTasksFromFile();

        // Loads tasks and initializes the JavaFX TableView with task properties
        TableColumn<Task, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<Task, String> dueDateColumn = new TableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(dateFormat.format(cellData.getValue().getDueDate())));

        TableColumn<Task, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));

        TableColumn<Task, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        tableView.getColumns().addAll(titleColumn, descriptionColumn, dueDateColumn, categoryColumn, statusColumn);
        updateTable();

        double buttonWidth = 120;

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getStyleClass().add("combo-box");
        // GUI Styling with CSS integration
        categoryFilter.getItems().addAll("All", "Personal", "Work", "Study");
        categoryFilter.setValue("All");
        categoryFilter.setOnAction(e -> filterByCategory(categoryFilter.getValue()));
        categoryFilter.setMinWidth(buttonWidth);

        // Creates functional buttons with assigned event handlers
        Button addButton = createButton("Add Task", buttonWidth, e -> addTask());
        Button removeButton = createButton("Remove Task", buttonWidth, e -> removeTask());
        Button editButton = createButton("Edit Task", buttonWidth, e -> editTask());
        Button completeButton = createButton("Mark as Completed", buttonWidth, e -> markTaskAsCompleted());
        Button sortButton = createButton("Sort by Due Date", buttonWidth, e -> showSortedTasks());
        Button filterCompletedButton = createButton("Show Completed", buttonWidth, e -> showCompletedTasks());
        Button filterPendingButton = createButton("Show Pending", buttonWidth, e -> showPendingTasks());
        Button filterDueTodayButton = createButton("Show Due Today", buttonWidth, e -> showDueTodayTasks());
        Button analyticsButton = createButton("Show Analytics", buttonWidth, e -> showAnalytics());
        Button downloadButton = createButton("Download Tasks", buttonWidth, e -> exportTasksToTextFile());
        Button exitButton = createButton("Exit", buttonWidth, e -> {
            //saveTasksToFile();
            System.exit(0);
        });


        // UI components arranged with VBox, HBox, and BorderPane
        VBox taskButtons = new VBox(10, addButton, removeButton, editButton, completeButton);
        taskButtons.setAlignment(Pos.CENTER_LEFT);

        VBox filterButtons = new VBox(10, filterCompletedButton, filterPendingButton, filterDueTodayButton, categoryFilter);
        filterButtons.setAlignment(Pos.CENTER);

        VBox otherButtons = new VBox(10, sortButton, analyticsButton, downloadButton, exitButton);
        otherButtons.setAlignment(Pos.CENTER_RIGHT);

        HBox buttonBox = new HBox(20, taskButtons, filterButtons, otherButtons);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, tableView, buttonBox);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 900, 550);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createButton(String text, double width, EventHandler<ActionEvent> action) {
        Button button = new Button(text);
        button.setMinWidth(width);
        button.setOnAction(action);
        return button;
    }

    private void showCategoryPieChart() {
        // Uses PieChart to visualize task categories
        List<Task> allTasks = DatabaseManager.getTasks();

        Map<String, Long> categoryCounts = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getCategory, Collectors.counting()));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Personal", categoryCounts.getOrDefault("Personal", 0L)),
                new PieChart.Data("Work", categoryCounts.getOrDefault("Work", 0L)),
                new PieChart.Data("Study", categoryCounts.getOrDefault("Study", 0L))
        );

        if (pieChartData.stream().allMatch(data -> data.getPieValue() == 0)) {
            showAlert("No task data available to display chart.");
            return;
        }

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Task Categories");

        Stage chartStage = new Stage();
        VBox chartLayout = new VBox(pieChart);
        chartLayout.setPadding(new Insets(10));
        Scene chartScene = new Scene(chartLayout, 400, 400);

        chartStage.setTitle("Task Category Distribution");
        chartStage.setScene(chartScene);
        chartStage.show();
    }


    private void filterByCategory(String category) {
        // Filtering tasks using Java Streams
        List<Task> filteredTasks = category.equals("All")
                ? DatabaseManager.getTasks()
                : DatabaseManager.getTasks().stream()
                .filter(task -> category.equalsIgnoreCase(task.getCategory()))
                .collect(Collectors.toList());

        tableView.setItems(FXCollections.observableArrayList(filteredTasks));
    }

    private void showSortedTasks() {
        // Sorting tasks by due date using Comparator
        List<Task> sortedTasks = DatabaseManager.getTasks().stream()
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());

        tableView.setItems(FXCollections.observableArrayList(sortedTasks));
    }

    private void showCompletedTasks() {
        List<Task> filteredTasks = DatabaseManager.getTasks().stream()
                .filter(task -> "Completed".equalsIgnoreCase(task.getStatus()))
                .collect(Collectors.toList());

        if (filteredTasks.isEmpty()) {
            showAlert("No completed tasks found.");
        } else {
            tableView.setItems(FXCollections.observableArrayList(filteredTasks));
        }
    }

    private void showPendingTasks() {
        List<Task> filteredTasks = DatabaseManager.getTasks().stream()
                .filter(task -> "Pending".equalsIgnoreCase(task.getStatus()))
                .collect(Collectors.toList());

        if (filteredTasks.isEmpty()) {
            showAlert("No pending tasks found.");
        } else {
            tableView.setItems(FXCollections.observableArrayList(filteredTasks));
        }
    }

    private void showDueTodayTasks() {
        List<Task> filteredTasks = taskManager.getDueTodayTasks();
        tableView.setItems(FXCollections.observableArrayList(filteredTasks));
    }

//    private void saveTasksToFile() {
//        File file = new File("tasks.json");
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
//            String json = new Gson().toJson(taskManager.getAllTasks());
//            writer.write(json);
//        } catch (IOException e) {
//            System.out.println("Error saving tasks: " + e.getMessage());
//        }
//    }

//    private void loadTasksFromFile() {
//        File file = new File("tasks.json");
//        if (!file.exists() || file.length() == 0) {
//            System.out.println("No previous tasks found. Starting fresh.");
//            return;
//        }
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            String json = reader.readLine();
//            if (json == null || json.isEmpty() || !json.startsWith("[") || !json.endsWith("]")) {
//                System.out.println("Invalid or empty task file. Skipping loading.");
//                return;
//            }
//
//            List<Task> loadedTasks = new Gson().fromJson(json, new TypeToken<List<Task>>() {}.getType());
//            taskManager.setTasks(loadedTasks);
//            updateTable();
//        } catch (IOException e) {
//            System.out.println("Error loading tasks: " + e.getMessage());
//        }
//    }

    private void addTask() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Task");

        dialog.setHeaderText("Enter Task Title:");
        String title = dialog.showAndWait().orElse(null);
        if (title == null || title.trim().isEmpty()) return;

        dialog.setHeaderText("Enter Task Description:");
        String description = dialog.showAndWait().orElse(null);

        dialog.setHeaderText("Enter Due Date (yyyy-MM-dd):");
        dialog.getEditor().setText("2025-03-20");
        String dueDate = dialog.showAndWait().orElse(null);

        if (dueDate == null || dueDate.trim().isEmpty()) {
            dueDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }

        ChoiceDialog<String> categoryDialog = new ChoiceDialog<>("Personal", "Personal", "Work", "Study");
        categoryDialog.setTitle("Select Category");
        categoryDialog.setHeaderText("Choose a category:");
        String category = categoryDialog.showAndWait().orElse(null);
        if (category == null) return;

        String status = "Pending";
        DatabaseManager.insertTask(title, description, dueDate, category, status);
        updateTable();
    }

    private void removeTask() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Please select a task to remove.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete this task?");
        confirmAlert.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            DatabaseManager.deleteTask(selectedTask.getId());
            updateTable();
        }
    }

    private void editTask() {
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Please select a task to edit.");
            return;
        }

        List<String> options = Arrays.asList("Title", "Description", "Due Date", "Category", "Status");
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Title", options);
        choiceDialog.setTitle("Edit Task");
        choiceDialog.setHeaderText("Select the field you want to edit:");

        String selectedOption = choiceDialog.showAndWait().orElse(null);
        if (selectedOption == null) return;

        String newValue = null;

        switch (selectedOption) {
            case "Title":
                newValue = showInputDialog("Edit Title", "Enter new Task Title:", selectedTask.getTitle());
                if (newValue == null || newValue.trim().isEmpty()) return;
                selectedTask.setTitle(newValue);
                break;

            case "Description":
                newValue = showInputDialog("Edit Description", "Enter new Task Description:", selectedTask.getDescription());
                if (newValue == null) return;
                selectedTask.setDescription(newValue);
                break;

            case "Due Date":
                newValue = showInputDialog("Edit Due Date", "Enter new Due Date (yyyy-MM-dd):",
                        new SimpleDateFormat("yyyy-MM-dd").format(selectedTask.getDueDate()));
                if (newValue == null) return;

                try {
                    Date newDueDate = new SimpleDateFormat("yyyy-MM-dd").parse(newValue);
                    selectedTask.setDueDate(newDueDate);
                } catch (ParseException e) {
                    showAlert("Invalid date format! Use yyyy-MM-dd.");
                    return;
                }
                break;

            case "Category":
                List<String> categories = Arrays.asList("Personal", "Work", "Study");
                ChoiceDialog<String> categoryDialog = new ChoiceDialog<>(selectedTask.getCategory(), categories);
                categoryDialog.setTitle("Edit Category");
                categoryDialog.setHeaderText("Select new category:");
                newValue = categoryDialog.showAndWait().orElse(null);
                if (newValue == null) return;
                selectedTask.setCategory(newValue);
                break;

            case "Status":
                List<String> statuses = Arrays.asList("Pending", "Completed");
                ChoiceDialog<String> statusDialog = new ChoiceDialog<>(selectedTask.getStatus(), statuses);
                statusDialog.setTitle("Edit Status");
                statusDialog.setHeaderText("Select new status:");
                newValue = statusDialog.showAndWait().orElse(null);
                if (newValue == null) return;
                selectedTask.setStatus(newValue);
                break;

            default:
                showAlert("Invalid option selected.");
                return;
        }

        DatabaseManager.updateTask(selectedTask.getId(), selectedOption, newValue);
        updateTable();
        tableView.refresh();
    }

    private String showInputDialog(String title, String header, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        return dialog.showAndWait().orElse(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateTable() {
        // Fetches tasks from MySQL using JDBC connection
        List<Task> tasks = DatabaseManager.getTasks();
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(tasks));
    }

    private void markTaskAsCompleted() {
        // Updating task status via user interaction
        Task selectedTask = tableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Please select a task to mark as completed.");
            return;
        }

        DatabaseManager.updateTaskStatus(selectedTask.getId(), "Completed");
        updateTable();
    }

    private void showAnalytics() {
        // Generates completion statistics using filtering and percentage calculation
        List<Task> allTasks = DatabaseManager.getTasks();

        long completedTasks = allTasks.stream().filter(task -> "Completed".equalsIgnoreCase(task.getStatus())).count();
        long pendingTasks = allTasks.stream().filter(task -> "Pending".equalsIgnoreCase(task.getStatus())).count();
        long overdueTasks = allTasks.stream()
                .filter(task -> task.getDueDate().before(new Date()) && "Pending".equalsIgnoreCase(task.getStatus()))
                .count();
        long totalTasks = allTasks.size();

        String statsMessage = String.format(
                "Total Tasks: %d\nCompleted: %d (%.2f%%)\nPending: %d (%.2f%%)\nOverdue: %d",
                totalTasks,
                completedTasks, (totalTasks == 0 ? 0 : (completedTasks * 100.0 / totalTasks)),
                pendingTasks, (totalTasks == 0 ? 0 : (pendingTasks * 100.0 / totalTasks)),
                overdueTasks
        );

        Alert statsAlert = new Alert(Alert.AlertType.INFORMATION);
        statsAlert.setTitle("Task Completion Statistics");
        statsAlert.setHeaderText("Task Completion Overview");
        statsAlert.setContentText(statsMessage);
        statsAlert.showAndWait();

        showCategoryPieChart();
    }

    private void exportTasksToTextFile() {
        // Exports tasks to a local .txt file
        List<Task> tasks = DatabaseManager.getTasks();
        if (tasks.isEmpty()) {
            showAlert("No tasks available to download.");
            return;
        }

        File file = new File("tasks.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Task List:\n");
            writer.write("===========================\n");

            for (Task task : tasks) {
                writer.write("Title: " + task.getTitle() + "\n");
                writer.write("Description: " + task.getDescription() + "\n");
                writer.write("Due Date: " + new SimpleDateFormat("yyyy-MM-dd").format(task.getDueDate()) + "\n");
                writer.write("Category: " + task.getCategory() + "\n");
                writer.write("Status: " + task.getStatus() + "\n");
                writer.write("---------------------------\n");
            }

            showAlert("Tasks successfully exported to tasks.txt!");
        } catch (IOException e) {
            showAlert("Error exporting tasks: " + e.getMessage());
        }
    }
}

