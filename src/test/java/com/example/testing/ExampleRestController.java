package com.example.testing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
public class ExampleRestController {

    @GetMapping("/example")
    public ResponseEntity<Map<String, Object>> getExample() {
        return ResponseEntity.ok(Map.of("message", "Hello World!"));
    }

    @GetMapping("/actuator/*")
    public ResponseEntity<Map<String, Object>> getActuator() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
