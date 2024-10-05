package edu.fish.blinddate.repository;

import edu.fish.blinddate.entity.BlindDateRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BlindDateRecordRepository extends JpaRepository<BlindDateRecord, Integer> {

    @Modifying
    @Transactional
    @Query("delete from BlindDateRecord where userId = ?1 and candidateId = ?2")
    void deleteByUserIdAndCandidateId(Integer userId, Integer candidateId);
}
