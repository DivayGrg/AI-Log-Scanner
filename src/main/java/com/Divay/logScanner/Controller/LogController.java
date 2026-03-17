package com.Divay.logScanner.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.Divay.logScanner.Service.LogService;
import com.Divay.logScanner.dto.LogAnalysisResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/upload")
    public List<LogAnalysisResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        // Service ko file ka input stream pass kar rahe hain
        return logService.analyzeLogs(file.getInputStream());
    }
}