package com.ocr_convert.controller;

import com.ocr_convert.service.OcrConvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OcrConvertController {

    private final OcrConvertService ocrConvertService;

    @PostMapping("/convert")
    public ResponseEntity<String> getConvertResult(@RequestBody String payload) {
        return ResponseEntity.ok(ocrConvertService.getConvertResult(payload));
    }

    @PostMapping("/convert/table")
    public ResponseEntity<String> getConvertTableResult(@RequestBody String payload) {
        return ResponseEntity.ok(ocrConvertService.getTableConvertResult(payload));
    }
}
