package com.raichung.shiftledger.common;

import org.springframework.stereotype.Component;

// Temporary: always the demo user. When we add login, only this class changes.
@Component
public class CurrentUser {
    public Long id() {
        return 1L;
    }
}
