package main;

// CS202: Importing the server logic and UI launcher
import networking.TaskServer;
import ui.TaskManagerFX;

public class Main {
    public static void main(String[] args) {

        // CS202: Threading – running server logic in parallel
        new Thread(() -> {
            try {
                // CS202: Optional – Simulated Client-Server Communication
                TaskServer.main(new String[]{});
            } catch (Exception e) {
                // CS202: Exception Handling
                e.printStackTrace();
            }
        }).start();

        // CS202: JavaFX – Application Entry Point
        TaskManagerFX.main(new String[]{});
    }
}
