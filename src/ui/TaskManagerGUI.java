/*package ui;

import model.Task;
import model.TaskManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskManagerGUI {
    private static DefaultTableModel tableModel;
    private static JTable table;
    private static TaskManager taskManager = new TaskManager();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // ✅ Formatter

    public static void main(String[] args) {
        JFrame frame = new JFrame("Task Manager");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // ✅ Table Model with Columns
        String[] columnNames = {"Title", "Description", "Due Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // ✅ Button Panel (Add, Remove, Edit, Complete, Sort, Filter, Exit)
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Task");
        JButton removeButton = new JButton("Remove Task");
        JButton editButton = new JButton("Edit Task");
        JButton completeButton = new JButton("Mark as Completed");

        JButton sortButton = new JButton("Sort by Due Date");
        JButton filterCompletedButton = new JButton("Show Completed");
        JButton filterPendingButton = new JButton("Show Pending");
        JButton filterDueTodayButton = new JButton("Show Due Today");
        JButton exitButton = new JButton("Exit"); // ✅ New Exit Button

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(editButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(filterCompletedButton);
        buttonPanel.add(filterPendingButton);
        buttonPanel.add(filterDueTodayButton);
        buttonPanel.add(exitButton); // ✅ Add Exit Button
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // ✅ Button Actions
        addButton.addActionListener(e -> addTask());
        removeButton.addActionListener(e -> removeTask());
        editButton.addActionListener(e -> editTask());
        completeButton.addActionListener(e -> markTaskAsCompleted());
        sortButton.addActionListener(e -> showSortedTasks());
        filterCompletedButton.addActionListener(e -> showCompletedTasks());
        filterPendingButton.addActionListener(e -> showPendingTasks());
        filterDueTodayButton.addActionListener(e -> showDueTodayTasks());
        exitButton.addActionListener(e -> exitApplication()); // ✅ New Action

        frame.add(panel);
        frame.setVisible(true);

        updateTable(); // ✅ Load tasks when GUI opens
    }

    // ✅ Method to Handle Exit Button
    private static void exitApplication() {
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            System.exit(0); // ✅ Close application
        }
    }

    // ✅ Method to Add a Task
    private static void addTask() {
        String title = JOptionPane.showInputDialog("Enter Task Title:");
        if (title == null || title.trim().isEmpty()) return;

        String description = JOptionPane.showInputDialog("Enter Task Description:");
        String dueDateStr = JOptionPane.showInputDialog("Enter Due Date (yyyy-MM-dd):");

        try {
            Date dueDate = dateFormat.parse(dueDateStr); // ✅ Convert String to Date
            Task task = new Task(title, description, dueDate, "Not Completed");
            taskManager.addTask(task);
            updateTable();
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid date format! Use yyyy-MM-dd.");
        }
    }

    // ✅ Method to Remove a Task
    private static void removeTask() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a task to remove.");
            return;
        }

        String title = (String) tableModel.getValueAt(selectedRow, 0);
        taskManager.removeTask(title);
        updateTable(); // ✅ Update table after removal
    }

    // ✅ Method to Edit a Task
    private static void editTask() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a task to edit.");
            return;
        }

        String oldTitle = (String) tableModel.getValueAt(selectedRow, 0);
        Task existingTask = taskManager.getTaskByTitle(oldTitle);
        if (existingTask == null) {
            JOptionPane.showMessageDialog(null, "Error: Task not found.");
            return;
        }

        String newTitle = JOptionPane.showInputDialog("Enter new Task Title:", existingTask.getTitle());
        String newDescription = JOptionPane.showInputDialog("Enter new Task Description:", existingTask.getDescription());
        String newDueDateStr = JOptionPane.showInputDialog("Enter new Due Date (yyyy-MM-dd):", dateFormat.format(existingTask.getDueDate()));

        try {
            Date newDueDate = dateFormat.parse(newDueDateStr);
            existingTask.setTitle(newTitle);
            existingTask.setDescription(newDescription);
            existingTask.setDueDate(newDueDate);
            updateTable(); // ✅ Update table after editing
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid date format! Use yyyy-MM-dd.");
        }
    }

    // ✅ Method to Mark Task as Completed
    private static void markTaskAsCompleted() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a task to mark as completed.");
            return;
        }

        String title = (String) tableModel.getValueAt(selectedRow, 0);
        taskManager.markTaskAsCompleted(title); // ✅ Now uses TaskManager method
        updateTable();
    }

    // ✅ Method to Show All Tasks (Default View)
    private static void updateTable() {
        tableModel.setRowCount(0);
        for (Task task : taskManager.getAllTasks()) {
            tableModel.addRow(new Object[]{
                    task.getTitle(),
                    task.getDescription(),
                    dateFormat.format(task.getDueDate()), // ✅ Format Due Date
                    task.isCompleted() ? "Completed" : "Not Completed"
            });
        }
    }

    // ✅ Sorting: Show Tasks Sorted by Due Date
    private static void showSortedTasks() {
        tableModel.setRowCount(0);
        for (Task task : taskManager.getTasksSortedByDate()) {
            tableModel.addRow(new Object[]{
                    task.getTitle(),
                    task.getDescription(),
                    dateFormat.format(task.getDueDate()), // ✅ Format Date
                    task.isCompleted() ? "Completed" : "Not Completed"
            });
        }
    }

    // ✅ Filtering: Show Only Completed Tasks
    private static void showCompletedTasks() {
        tableModel.setRowCount(0);
        for (Task task : taskManager.getCompletedTasks()) {
            tableModel.addRow(new Object[]{
                    task.getTitle(),
                    task.getDescription(),
                    dateFormat.format(task.getDueDate()), // ✅ Format Date
                    "Completed"
            });
        }
    }

    // ✅ Filtering: Show Only Pending Tasks
    private static void showPendingTasks() {
        tableModel.setRowCount(0);
        for (Task task : taskManager.getPendingTasks()) {
            tableModel.addRow(new Object[]{
                    task.getTitle(),
                    task.getDescription(),
                    dateFormat.format(task.getDueDate()), // ✅ Format Date
                    "Not Completed"
            });
        }
    }

    // ✅ Filtering: Show Only Tasks Due Today
    private static void showDueTodayTasks() {
        tableModel.setRowCount(0);
        for (Task task : taskManager.getDueTodayTasks()) {
            tableModel.addRow(new Object[]{
                    task.getTitle(),
                    task.getDescription(),
                    dateFormat.format(task.getDueDate()), // ✅ Format Date
                    task.isCompleted() ? "Completed" : "Not Completed"
            });
        }
    }
}
*/