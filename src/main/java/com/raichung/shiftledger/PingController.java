package com.raichung.shiftledger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    record PingResponse(String status, String app) {}

    @GetMapping("/api/v1/ping")
    public PingResponse ping() {
        return new PingResponse("ok", "ShiftLedger");
    }
}
