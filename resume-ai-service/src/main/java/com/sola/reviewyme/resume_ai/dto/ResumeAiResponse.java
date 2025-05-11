package com.sola.reviewyme.resume_ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sola.reviewyme.resume_ai.dto.ResumeAiRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumeAiResponse {
    private ResumeAiRequest.ContactDetails contactDetails;
    private String linkedinProfile;
    private List<String> portfolioLinks;
    private String professionalSummary;
    private List<String> skills;
    private List<ResumeAiRequest.WorkExperience> workExperience;
    private List<ResumeAiRequest.Education> education;
    private String relevantCourseWork;
    private List<ResumeAiRequest.Certification> certifications;
    private List<ResumeAiRequest.Reference> reference;


    }