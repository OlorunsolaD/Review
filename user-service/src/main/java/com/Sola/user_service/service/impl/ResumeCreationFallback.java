//package com.Sola.user_service.service.impl;
//
//import com.Sola.resume_creation_service.dto.ResumeCreationRequest;
//import com.Sola.resume_creation_service.model.Resume;
//import com.Sola.user_service.service.ResumeCreationClient;
//
//import java.util.List;
//
//public class ResumeCreationFallback implements ResumeCreationClient {
//
//    public Resume createResume(ResumeCreationRequest resumeCreationRequest) {
//        throw new RuntimeException("Fallback: Resume creation service is unavailable.");
//    }
//
//    @Override
//    public Resume updateResume(Long id, ResumeCreationRequest resumeCreationRequest) {
//
//        throw new RuntimeException("Fallback: Resume updated service is unavailable.");
//    }
//
//    @Override
//    public void markResumeUnderReview(Long id) {
//        throw new RuntimeException("Fallback: Unable to mark resume as UNDER_REVIEW");
//    }
//
//    @Override
//    public void markResumeCompleted(Long id) {
//        throw new RuntimeException("Fallback: Unable to mark resume as COMPLETED");
//    }
//
//    @Override
//    public Resume getResumeById(Long id) {
//
//        throw new RuntimeException("Fallback: Unable to retrieve resume.");
//    }
//
//    @Override
//    public List<Resume> getAllResumes() {
//        throw new RuntimeException("Fallback: Unable to retrieve all resumes.");
//    }
//
//    @Override
//    public void deleteResumeById(Long id) {
//        throw new RuntimeException("Fallback: Unable to delete resume.");
//
//    }
//
//}
