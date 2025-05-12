package com.sola.reviewyme.resume_ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class ResumeAiRequest {

private ContactDetails contactDetails;
private String jobUrl;
private String linkedinProfile;
private String professionalSummary; // Optional for manual input
private List<String> Skills; // Optional for manual input
private List<String> portfolioLinks;
private String jobDescription;
private boolean hasExistingResume;
private String resumeField;
private List<WorkExperience> workExperience;
private List<Education> education;
private String relevantCourseWork;
private List<Certification> certifications;
private List<Reference> reference;
public ResumeAiRequest() {

}

@Data
public static class ContactDetails {
    private String fullName;
    private String address;
    private String phone;
    private String email;
}

@Data
public static class WorkExperience {
    private String company;
    private String position;
    private String startDate;
    private String endDate;
    private List<String> responsibilities; // Optional for manual input
}

@Data
public static class Education {
    private String institution;
    private String degree;
    private String startDate;
    private String endDate;
}

@Data
public static class Certification {
    private String title;
    private String issuer;
    private String date;
}

@Data
public static class Reference {
    private String name;
    private String position;
    private String company;
    private String contact;
}
}
