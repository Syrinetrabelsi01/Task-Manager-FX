package model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import persistence.TaskStorage;

// TaskManager handles business logic and task filtering/sorting operations
public class TaskManager {

    // Stores all tasks in memory
    private List<Task> taskList;

    // Loads tasks from persistent storage on initialization
    public TaskManager() {
        taskList = TaskStorage.loadTasks();
    }

    // Adds a task and persists the updated list
    public void addTask(Task task) {
        taskList.add(task);
        TaskStorage.saveTasks(taskList);
    }

    // Removes a task by title and updates storage
    public void removeTask(String title) {
        taskList.removeIf(task -> task.getTitle().equals(title));
        TaskStorage.saveTasks(taskList);
    }

    // Returns all tasks
    public List<Task> getAllTasks() {
        return taskList;
    }

    // Searches for a task by title using Stream API
    public Task getTaskByTitle(String title) {
        return taskList.stream()
                .filter(task -> task.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }

    // Sorts tasks by due date
    public void sortByDueDate() {
        taskList.sort(Comparator.comparing(Task::getDueDate));
    }

    // Filters and returns only completed tasks
    public List<Task> getCompletedTasks() {
        return taskList.stream()
                .filter(task -> "Completed".equalsIgnoreCase(task.getStatus()))
                .collect(Collectors.toList());
    }

    // Filters and returns only pending tasks
    public List<Task> getPendingTasks() {
        return taskList.stream()
                .filter(task -> !"Completed".equalsIgnoreCase(task.getStatus()))
                .collect(Collectors.toList());
    }

    // Marks a task as completed by title and saves changes
    public void markTaskAsCompleted(String title) {
        Task task = getTaskByTitle(title);
        if (task != null) {
            task.setStatus("Completed");
            TaskStorage.saveTasks(taskList);
        }
    }

    // Returns tasks that are due today
    public List<Task> getDueTodayTasks() {
        LocalDate today = LocalDate.now();
        return taskList.stream()
                .filter(task -> {
                    LocalDate taskDate = task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return taskDate.isEqual(today);
                })
                .collect(Collectors.toList());
    }

    // Filters tasks by category
    public List<Task> getTasksByCategory(String category) {
        return taskList.stream()
                .filter(task -> task.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    // Filters tasks by tag
    public List<Task> getTasksByTag(String tag) {
        return taskList.stream()
                .filter(task -> task.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    // Saves the current state of task list
    public void saveTasks() {
        TaskStorage.saveTasks(taskList);
    }

    // Converts the task list into a readable string
    @Override
    public String toString() {
        return taskList.stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
    }

    // Replaces the current task list with a new one
    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
    }
}
