package networking;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import model.Task;

public class DatabaseManager {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/task_manager?serverTimezone=UTC&useSSL=false", "root", "");
    }

    public static void insertTask(String title, String description, String dueDate, String category, String status) {
        String sql = "INSERT INTO tasks (title, description, due_date, category, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, dueDate);
            pstmt.setString(4, category);
            pstmt.setString(5, status);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Task added successfully.");
            } else {
                System.out.println("Task was not added.");
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public static List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String status = rs.getString("status");
                if (status == null || status.trim().isEmpty()) {
                    status = "Pending";
                }

                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("due_date")),
                        rs.getString("category"),
                        status
                );
                tasks.add(task);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving tasks: " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Error parsing due_date: " + e.getMessage());
        }

        return tasks;
    }

    private static String truncateString(String str, int length) {
        if (str == null) return "";
        return (str.length() > length) ? str.substring(0, length - 3) + "..." : str;
    }

    public static void updateTask(int taskId, String column, String newValue) {
        String sql = "UPDATE tasks SET " + column + " = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newValue);
            pstmt.setInt(2, taskId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Task updated successfully.");
            } else {
                System.out.println("Task not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
        }
    }

    public static void updateTaskStatus(int taskId, String newStatus) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, taskId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Task status updated successfully.");
            } else {
                System.out.println("Task not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating task status: " + e.getMessage());
        }
    }

    public static void deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Task deleted successfully.");
            } else {
                System.out.println("Task not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting task: " + e.getMessage());
        }
    }
}
