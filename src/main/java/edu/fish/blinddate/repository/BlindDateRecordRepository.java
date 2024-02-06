package edu.fish.blinddate.repository;

import edu.fish.blinddate.entity.BlindDateRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlindDateRecordRepository extends JpaRepository<BlindDateRecord, Integer> {
}
