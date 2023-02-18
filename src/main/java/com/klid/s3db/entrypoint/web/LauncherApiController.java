package com.klid.s3db.entrypoint.web;

import com.klid.s3db.service.LaunchProcessCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Ivan Kaptue
 */
@RequiredArgsConstructor
@RestController
public class LauncherApiController {

    private final LaunchProcessCommand launchProcessCommand;

    @PostMapping("/download")
    public ResponseEntity<Map<String, Long>> launch(@RequestParam(value = "filename") String filename) {
        var result = launchProcessCommand.execute(filename);
        return ResponseEntity.ok(Map.of("sales", result));
    }
}
