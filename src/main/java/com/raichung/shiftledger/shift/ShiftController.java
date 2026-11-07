package com.raichung.shiftledger.shift;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateShiftResponse create(@Valid @RequestBody CreateShiftRequest request) {
        return shiftService.create(request);
    }

    @GetMapping
    public List<ShiftResponse> list() {
        return shiftService.list();
    }
}
