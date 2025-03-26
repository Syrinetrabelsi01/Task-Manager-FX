package model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import persistence.TaskStorage;

public class TaskManager {
    private List<Task> taskList;

    public TaskManager() {
        taskList = TaskStorage.loadTasks();
    }

    public void addTask(Task task) {
        taskList.add(task);
        TaskStorage.saveTasks(taskList);
    }

    public void removeTask(String title) {
        taskList.removeIf(task -> task.getTitle().equals(title));
        TaskStorage.saveTasks(taskList);
    }

    public List<Task> getAllTasks() {
        return taskList;
    }

    public Task getTaskByTitle(String title) {
        return taskList.stream()
                .filter(task -> task.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }

    public void sortByDueDate() {
        taskList.sort(Comparator.comparing(Task::getDueDate));
    }

    public List<Task> getCompletedTasks() {
        return taskList.stream()
                .filter(task -> "Completed".equalsIgnoreCase(task.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Task> getPendingTasks() {
        return taskList.stream()
                .filter(task -> !"Completed".equalsIgnoreCase(task.getStatus()))
                .collect(Collectors.toList());
    }

    public void markTaskAsCompleted(String title) {
        Task task = getTaskByTitle(title);
        if (task != null) {
            task.setStatus("Completed");
            TaskStorage.saveTasks(taskList);
        }
    }

    public List<Task> getDueTodayTasks() {
        LocalDate today = LocalDate.now();
        return taskList.stream()
                .filter(task -> {
                    LocalDate taskDate = task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return taskDate.isEqual(today);
                })
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByCategory(String category) {
        return taskList.stream()
                .filter(task -> task.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByTag(String tag) {
        return taskList.stream()
                .filter(task -> task.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    public void saveTasks() {
        TaskStorage.saveTasks(taskList);
    }

    @Override
    public String toString() {
        return taskList.stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
    }
}
