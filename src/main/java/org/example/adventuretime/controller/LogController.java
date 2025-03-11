package org.example.adventuretime.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {

    @GetMapping("/logs")
    public ResponseEntity<byte[]> getLogFile(@RequestParam String date) {
        String logFileName = "logs/adventuretime-" + date + ".log";
        Path logFilePath = Paths.get(logFileName);

        if (Files.exists(logFilePath)) {
            try (FileInputStream inputStream = new FileInputStream(logFilePath.toFile())) {
                byte[] logFileBytes = inputStream.readAllBytes();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_PLAIN);
                headers.setContentDispositionFormData("attachment",
                        "adventuretime-" + date + ".log");
                return new ResponseEntity<>(logFileBytes, headers, HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}