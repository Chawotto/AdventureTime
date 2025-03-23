package org.example.adventuretime.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {
    @Operation(summary = "Request log file sorted by date and logging level")
    @GetMapping("/logs")
    public ResponseEntity<byte[]> getLogFile(
            @RequestParam String date,
            @RequestParam(required = false, defaultValue = "all") String level) {

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String logFileName = "logs/adventuretime-" + date + ".log";
        Path logFilePath = Paths.get(logFileName).normalize();

        if (Files.exists(logFilePath)) {
            try (Stream<String> linesStream = Files.lines(logFilePath, StandardCharsets.UTF_8)) {
                List<String> lines;

                if (!"all".equalsIgnoreCase(level)) {
                    String logLevel = level.toUpperCase();
                    Pattern logPattern = Pattern.compile(
                            "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} "
                                    + logLevel + " ");

                    lines = linesStream.filter(line -> logPattern.matcher(line).find())
                            .toList();
                } else {
                    lines = linesStream.toList();
                }

                byte[] logFileBytes = String.join("\n", lines)
                        .getBytes(StandardCharsets.UTF_8);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_PLAIN);
                headers.setContentDispositionFormData("attachment",
                        "adventuretime-" + date + "-" + level + ".log");

                return new ResponseEntity<>(logFileBytes, headers, HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
