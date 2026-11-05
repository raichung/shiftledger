package com.raichung.shiftledger.job;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findAllByUserIdOrderByEmployerNameAsc(Long userId);
    Optional<Job> findByIdAndUserId(Long id, Long userId);
}
