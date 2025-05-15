package com.sola.reviewyme.resume_ai.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.File;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@Document(collection = "resumes") // MongoDB Collection name
public class ResumeAi {

@Id // Mongo primary key
private String resumeId;
private ContactDetails contactDetails;
private String jobUrl; // Optional for manual input
private String linkedinProfile;
private String professionalSummary; // Optional for manual input
private List<String> Skills; // Optional for manual input
private List<String> portfolioLinks;
private String jobDescription;
private boolean hasExistingResume;
private List<WorkExperience> workExperience;
private List<Education> education;
private String relevantCourseWork;
private List<Certification> certifications;
private List<Reference> reference;
public ResumeAi() {

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class ContactDetails {
    private String fullName;
    private String address;
    private String phone;
    private String email;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class WorkExperience {
    private String company;
    private String position;
    private String startDate;
    private String endDate;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> responsibilities; // Optional for manual input
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class Education {
    private String institution;
    private String degree;
    private String startDate;
    private String endDate;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class Certification {
    private String title;
    private String issuer;
    private String date;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class Reference {
    private String name;
    private String position;
    private String company;
    private String contact;
}
}
