package com.sola.reviewyme.resume_ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sola.reviewyme.resume_ai.model.ResumeAi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumeAiResponse {
    private ResumeAi.ContactDetails contactDetails;
    private String linkedinProfile;
    private List<String> portfolioLinks;
    private String professionalSummary;
    private List<String> skills;
    private List<ResumeAi.WorkExperience> workExperience;
    private List<ResumeAi.Education> education;
    private String relevantCourseWork;
    private List<ResumeAi.Certification> certifications;
    private List<ResumeAi.Reference> reference;

    }