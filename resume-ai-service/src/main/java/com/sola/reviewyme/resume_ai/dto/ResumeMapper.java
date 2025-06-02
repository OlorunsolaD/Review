package com.sola.reviewyme.resume_ai.dto;

import com.sola.reviewyme.resume_ai.model.ResumeAi;
import org.springframework.stereotype.Component;

@Component
public class ResumeMapper {

    public ResumeAi toModel (ResumeAiResponse response) {
        return ResumeAi.builder()
//                .resumeId(response.toString())
                .contactDetails(response.getContactDetails())
                .contactDetails(response.getContactDetails())
                .linkedinProfile(response.getLinkedinProfile())
                .portfolioLinks(response.getPortfolioLinks())
                .professionalSummary(response.getProfessionalSummary())
                .Skills(response.getSkills())
                .workExperience(response.getWorkExperience())
                .education(response.getEducation())
                .relevantCourseWork(response.getRelevantCourseWork())
                .certifications(response.getCertifications())
                .reference(response.getReference())
                .build();
    }

    public ResumeAiResponse toResponse (ResumeAi resumeAi) {
        return ResumeAiResponse.builder()
                .contactDetails(resumeAi.getContactDetails())
                .linkedinProfile(resumeAi.getLinkedinProfile())
                .portfolioLinks(resumeAi.getPortfolioLinks())
                .professionalSummary(resumeAi.getProfessionalSummary())
                .skills(resumeAi.getSkills())
                .workExperience(resumeAi.getWorkExperience())
                .education(resumeAi.getEducation())
                .relevantCourseWork(resumeAi.getRelevantCourseWork())
                .certifications(resumeAi.getCertifications())
                .reference(resumeAi.getReference())
                .build();
    }

}
