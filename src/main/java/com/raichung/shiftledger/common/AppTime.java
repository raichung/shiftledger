package com.raichung.shiftledger.common;

import java.time.ZoneId;

public final class AppTime {

    // One time zone for now. Later each user can have their own.
    public static final ZoneId ZONE = ZoneId.of("Australia/Adelaide");

    private AppTime() {}
}
