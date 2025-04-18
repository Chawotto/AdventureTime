package org.example.adventuretime.logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.example.adventuretime.model.LogFileTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogFileGenerator {

    private static final Logger logger = LoggerFactory.getLogger(LogFileGenerator.class);

    @Async("taskExecutor")
    public void generateLogFileAsync(LogFileTask task, String date, String level) throws InterruptedException {
        Thread.sleep(30000);
        logger.info("Starting log file generation for task {} in thread {}",
                task.getTaskId(), Thread.currentThread().getName());
        try {
            String logFileName = "logs/adventuretime-" + date + ".log";
            Path logFilePath = Paths.get(logFileName).normalize();

            if (!Files.exists(logFilePath)) {
                task.setStatus("FAILED");
                task.setErrorMessage("Log file for date " + date + " not found");
                return;
            }

            Path outputPath = Paths.get("logs/task-" + task.getTaskId()
                    + "-" + date + "-" + level + ".log");
            try (Stream<String> linesStream = Files.lines(logFilePath, StandardCharsets.UTF_8)) {
                List<String> lines;

                if (!"all".equalsIgnoreCase(level)) {
                    String logLevel = level.toUpperCase();
                    Pattern logPattern = Pattern.compile(
                            "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} "
                                    + logLevel + " ");
                    lines = linesStream.filter(line -> logPattern.matcher(line).find()).toList();
                } else {
                    lines = linesStream.toList();
                }

                Files.write(outputPath, lines, StandardCharsets.UTF_8);
                task.setFilePath(outputPath);
                task.setStatus("COMPLETED");
                logger.info("Log file generation completed for task {}", task.getTaskId());
            }
        } catch (IOException e) {
            logger.error("Error generating log file for task {}: {}",
                    task.getTaskId(), e.getMessage());
            task.setStatus("FAILED");
            task.setErrorMessage("Failed to generate log file: " + e.getMessage());
        }
    }
}