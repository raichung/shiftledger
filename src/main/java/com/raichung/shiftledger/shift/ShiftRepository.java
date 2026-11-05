package com.raichung.shiftledger.shift;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findAllByJob_UserIdOrderByStartTimeDesc(Long userId);
}
