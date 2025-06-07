// src/main/java/com/reviewyme/repository/ExpertUserMappingRepository.java
package com.reviewyme.userservice.repository;

import com.reviewyme.userservice.model.ExpertUserMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpertUserMappingRepository extends MongoRepository<ExpertUserMapping, String> {
    // Example query: Find experts sorted by the number of assigned resumes (requires 'assignedCount' field or a custom query)
    // If 'assignedResumeIds' is an array, MongoDB can directly sort by its size or you query via aggregation.
    // A more practical approach might be to store a 'assignedCount' field and index it.
    // Example: List<ExpertUserMapping> findByOrderByAssignedResumeIdsSizeAsc(); // This isn't directly supported by Spring Data.
    // You'd typically add a field `private int assignedCount;` to ExpertUserMapping and then:
    // List<ExpertUserMapping> findByOrderByAssignedCountAsc();

    // For now, let's just assume simple queries or aggregation will be used later.
    // The main benefit is the *separate collection*.
    Optional<ExpertUserMapping> findByUserId(String userId);

    // You can add custom queries for finding experts based on criteria
    // e.g., @Query(value = "{ 'assignedResumeIds' : { $size: { $min: 0 } } }", sort = "{ 'assignedResumeIds.$size' : 1 }")
    // This sort by size is complex and often done via aggregation pipeline in MongoDB
    // For sorting, a separate `assignedCount` field in ExpertUserMapping would be highly performant.
}