package persistence;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

// Handles backup and recovery of tasks using JSON serialization
public class TaskStorage {

    // JSON file used for storing tasks
    private static final String FILE_NAME = "tasks.json";

    // Configures the ObjectMapper for readable JSON and proper date formatting
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setDateFormat(new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a"));

    /**
     * Saves the list of tasks to a local JSON file.
     * Demonstrates file I/O and object serialization with Jackson.
     */
    public static void saveTasks(List<Task> tasks) {
        try {
            objectMapper.writeValue(new File(FILE_NAME), tasks);
            System.out.println("Tasks backed up to tasks.json.");
        } catch (IOException e) {
            // Handles file writing exceptions
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Loads tasks from the local JSON file.
     * Returns an empty list if file is missing or corrupted.
     */
    public static List<Task> loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            // File not found: return empty list to avoid errors
            System.out.println("tasks.json not found. Returning empty list.");
            return new ArrayList<>();
        }

        try {
            // Deserialize JSON array into a list of Task objects
            System.out.println("Loaded tasks from backup.");
            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class));
        } catch (IOException e) {
            // Handles file reading or parsing issues
            System.err.println("Error loading tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
