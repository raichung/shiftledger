package com.raichung.shiftledger.shift;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class FortnightHoursCheckerTest {

    @Test
    void saturdaySitsInTwoFortnights() {
        //saturday 7 Nov 2026 is in the fortnights starting Mon 26 Oct and Mon 2 Nov
        assertThat(FortnightHoursChecker.fortnightStartsFor(LocalDate.of(2026, 11, 7)))
                .containsExactly(LocalDate.of(2026, 10, 26), LocalDate.of(2026, 11, 2));
    }

    @Test
    void mondayStartsANewFortnight() {
        //monday 9 Nov 2026 is in the fortnights starting Mon 2 Nov and Mon 9 Nov
        assertThat(FortnightHoursChecker.fortnightStartsFor(LocalDate.of(2026, 11, 9)))
                .containsExactly(LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 9));
    }
}