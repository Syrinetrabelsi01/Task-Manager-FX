package networking;

public class TestDatabaseOperations {
    public static void main(String[] args) {
        DatabaseManager.insertTask("Project", "do project java", "2025-03-20", "Personal", "Pending");
        DatabaseManager.getTasks();
        DatabaseManager.updateTaskStatus(1, "Completed");
        DatabaseManager.deleteTask(1);
    }
}
