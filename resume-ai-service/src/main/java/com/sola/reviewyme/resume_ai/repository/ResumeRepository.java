package com.sola.reviewyme.resume_ai.repository;

import com.sola.reviewyme.resume_ai.model.ResumeAi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<ResumeAi, String> {

    Optional<ResumeAi> findByResumeId (String resumeId);
}
