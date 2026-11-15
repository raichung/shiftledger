package com.raichung.shiftledger.shift;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Shifts")
@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    @Operation(summary = "Log a shift",
            description = "Returns the pay for the shift and any 48-hour fortnight warnings.")

    @ResponseStatus(HttpStatus.CREATED)
    public CreateShiftResponse create(@Valid @RequestBody CreateShiftRequest request) {
        return shiftService.create(request);
    }

    @GetMapping
    public List<ShiftResponse> list() {
        return shiftService.list();
    }
}
