package org.example.adventuretime.model;

import java.nio.file.Path;
import lombok.Data;

@Data
public class LogFileTask {
    private String taskId;
    private String status;
    private Path filePath;
    private String errorMessage;

    public LogFileTask(String taskId) {
        this.taskId = taskId;
        this.status = "PENDING";
    }
}