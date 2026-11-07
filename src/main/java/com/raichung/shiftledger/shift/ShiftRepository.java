package com.raichung.shiftledger.shift;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findAllByJob_UserIdOrderByStartTimeDesc(Long userId);

    // Two shifts overlap if each one starts before the other ends
    @Query("""
            select count(s) from Shift s
            where s.job.userId = :userId
              and s.startTime < :endTime
              and s.endTime > :startTime
            """)
    long countOverlapping(Long userId, Instant startTime, Instant endTime);

    //all shifts for this user that start inside a time window
    @Query("""
            select s from Shift s
            where s.job.userId = :userId
              and s.startTime >= :from
              and s.startTime < :to
            """)
    List<Shift> findAllStartingBetween(Long userId, Instant from, Instant to);
}
