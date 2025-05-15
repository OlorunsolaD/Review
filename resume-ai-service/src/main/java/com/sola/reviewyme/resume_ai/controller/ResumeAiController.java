package com.sola.reviewyme.resume_ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sola.reviewyme.resume_ai.model.ResumeAi;
import com.sola.reviewyme.resume_ai.dto.ResumeAiResponse;
import com.sola.reviewyme.resume_ai.service.ResumeAiService;
import jakarta.validation.Valid;
import org.apache.tika.exception.TikaException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/resume")
public class ResumeAiController {

    private final ResumeAiService resumeAiService;
    private final ObjectMapper objectMapper;

    public ResumeAiController(ResumeAiService resumeAiService, ObjectMapper objectMapper) {
        this.resumeAiService = resumeAiService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/tailored/{userId}")
    public ResponseEntity<ResumeAiResponse> createTailoredResume(
            @PathVariable String userId,
            @RequestBody @Valid ResumeAi request) throws IOException {
        ResumeAiResponse response = resumeAiService.buildTailoredResume(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeAiResponse> getResumeById(@PathVariable String resumeId){

        ResumeAiResponse resumeAiResponse = resumeAiService.getResumeById(resumeId);
        return ResponseEntity.ok(resumeAiResponse);
    }

    @PostMapping
    public ResponseEntity<ResumeAi> saveResume(@RequestBody ResumeAi resumeAi) {
        ResumeAi savedResume = resumeAiService.saveResume(resumeAi);
        return ResponseEntity.ok(savedResume);
    }

    @PostMapping("/file/upload")
    public ResponseEntity<ResumeAiResponse> previewResume(@RequestParam("file") MultipartFile file) throws TikaException, IOException {
       // This service method parses the file upload and builds a ResumeAiResponse without AI enhancements.
            ResumeAiResponse previewResponse = resumeAiService.uploadResumeFile(file);
            return ResponseEntity.ok(previewResponse);
        }

    @PostMapping("/file-upload/update")
    public ResponseEntity<ResumeAiResponse> updateResumeForJob(
            @RequestParam("currentResumeJson") @Valid String currentResumeJson,
            @RequestParam("jobDescription") String jobDescription) throws IOException {

        ResumeAiResponse currentResume = objectMapper.readValue(currentResumeJson, ResumeAiResponse.class);
        ResumeAiResponse updatedResume = resumeAiService.updateResumeForJob(currentResume, jobDescription);

        return ResponseEntity.ok(updatedResume);
    }

    @DeleteMapping("/delete/{resumeId}")
    public ResponseEntity<Map<String, String>> deleteResumeById (@PathVariable String resumeId){
        resumeAiService.deleteByResumeId(resumeId);
        Map<String, String> response = Map.of("message", "Resume deleted successfully");
        return ResponseEntity.ok(response);
    }

}