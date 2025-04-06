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

public class TaskStorage {
    private static final String FILE_NAME = "tasks.json";

    // Use a matching date format for backup compatibility
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setDateFormat(new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a"));

    /**
     * Save tasks to a local JSON file (backup only).
     */
    public static void saveTasks(List<Task> tasks) {
        try {
            objectMapper.writeValue(new File(FILE_NAME), tasks);
            System.out.println("Tasks backed up to tasks.json.");
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Load tasks from local file (for offline recovery or backup restore).
     */
    public static List<Task> loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("tasks.json not found. Returning empty list.");
            return new ArrayList<>();
        }

        try {
            System.out.println("Loaded tasks from backup.");
            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class));
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
