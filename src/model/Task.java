package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String title;
    private String description;
    private Date dueDate;
    private String assignedUser;
    private String status;
    private String category;
    private List<String> tags;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Task() {
        this.tags = new ArrayList<>();
    }

    public Task(int id, String title, String description, Date dueDate, String category, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.status = (status == null || status.isEmpty()) ? "Pending" : status;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Date getDueDate() { return dueDate; }
    public String getAssignedUser() { return assignedUser; }
    public String getStatus() { return status; }
    public String getCategory() { return category; }
    public List<String> getTags() { return tags; }
    public int getId() { return id; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public void setAssignedUser(String assignedUser) { this.assignedUser = assignedUser; }
    public void setStatus(String status) { this.status = status; }
    public void setCategory(String category) { this.category = category; }

    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public String getFormattedTags() {
        return tags.isEmpty() ? "No Tags" : String.join(", ", tags);
    }

    public void markCompleted() {
        this.status = "Completed";
    }

    public String getFormattedDueDate() {
        return dateFormat.format(dueDate);
    }

    @Override
    public String toString() {
        return title + " (" + category + ") - Due: " + getFormattedDueDate() + " - Status: " + status + " - Tags: " + getFormattedTags();
    }
}
