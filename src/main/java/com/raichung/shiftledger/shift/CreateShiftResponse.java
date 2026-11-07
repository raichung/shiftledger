package com.raichung.shiftledger.shift;

import java.util.List;

public record CreateShiftResponse(ShiftResponse shift, List<String> warnings) {}