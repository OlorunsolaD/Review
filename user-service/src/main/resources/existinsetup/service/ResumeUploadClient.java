//package com.Sola.user_service.service;
//
//
//import com.Sola.resume_upload_service.dto.ResumeUploadRequest;
//import com.Sola.resume_upload_service.model.ResumeUploadEntity;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@FeignClient(name = "resume-upload-service", path = "/api/v2/resumes")
//public interface ResumeUploadClient {
//
//    @PostMapping(value = "/uploadCV", consumes = "multipart/form-data")
//    ResponseEntity<ResumeUploadEntity> uploadResume(@ModelAttribute ResumeUploadRequest resumeUploadRequest);
//
//    @PutMapping(value = "/updateCV/{id}", consumes = "multipart/form-data")
//    ResponseEntity<String> updateResume (@PathVariable("id") @RequestParam("file") MultipartFile file);
//
//    @GetMapping("/findCV/{id}")
//    ResponseEntity<ResumeUploadEntity> getResumeById (@PathVariable("id") String id);
//
//    @GetMapping("/userCV/{userId}")
//    ResponseEntity<List<ResumeUploadEntity>> getAllResumesByUserId (@PathVariable String userId);
//
//    @DeleteMapping("/deleteCV/{id}")
//    ResponseEntity<String> deleteResumeById(@PathVariable("id") String id);
//}
