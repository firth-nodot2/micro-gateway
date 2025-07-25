package org.rhydo.microgateway.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/products")
    public ResponseEntity<String> productsFallback() {
        return new ResponseEntity<>("Product service is unavailable, please try after sometime", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
