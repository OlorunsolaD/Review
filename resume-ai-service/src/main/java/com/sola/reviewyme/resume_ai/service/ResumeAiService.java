package com.sola.reviewyme.resume_ai.service;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sola.reviewyme.resume_ai.dto.ResumeMapper;
import com.sola.reviewyme.resume_ai.exception.ResumeNotFoundException;
import com.sola.reviewyme.resume_ai.model.ResumeAi;
import com.sola.reviewyme.resume_ai.dto.ResumeAiResponse;
import com.sola.reviewyme.resume_ai.dto.User;
import com.sola.reviewyme.resume_ai.repository.ResumeRepository;
import com.sola.reviewyme.resume_ai.utils.ResumeParser;
import org.apache.tika.exception.TikaException;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeAiService {

    @Autowired
    private final ResumeRepository resumeRepository;
    private final ResumeMapper resumeMapper;


    @Autowired
    private final RestTemplate restTemplate;
    private static final String USER_SERVICE_URL = "http://localhost:8031/api/v1/user/{userId}";
    private final OpenAiApi openAiApi;
    private final ObjectMapper objectMapper;

    public ResumeAiService(ResumeRepository resumeRepository, ResumeMapper resumeMapper,
                           RestTemplate restTemplate, @Value("${ai.openai.api-key}") String openApiApiKey) {
        this.resumeMapper = resumeMapper;
        this.resumeRepository = resumeRepository;
        this.restTemplate = restTemplate;
        this.openAiApi = new OpenAiApi(openApiApiKey);
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ResumeAiResponse buildTailoredResume(ResumeAi request, String userId) throws IOException {
        System.out.println("Fetching user with Id" + userId);

        String url = USER_SERVICE_URL.replace("{userId}", userId);
        ResponseEntity<User> userResponseEntity = restTemplate.getForEntity(url, User.class);

        if (userResponseEntity.getStatusCode().is2xxSuccessful()) {
            User user = userResponseEntity.getBody();


        }
        // Use AI to generate summary only if it's not provided
        String aiGeneratedSummary = (request.getProfessionalSummary() == null || request.getProfessionalSummary().isEmpty()
                ? generateSummaryUsingAI(request.getJobDescription(), request.getWorkExperience())
                : request.getProfessionalSummary());

        // Use AI to generate skills only if they're not provided
        List<String> aiGeneratedSkills = (request.getSkills() == null || request.getSkills().isEmpty()
                ? generateSkillsUsingAI(request.getJobDescription(), request.getWorkExperience())
                : request.getSkills());

        // Generate AI responsibilities only if they are not provided
        List<ResumeAi.WorkExperience> updatedWorkExperiences = new ArrayList<>();
        for (ResumeAi.WorkExperience workExp : request.getWorkExperience()) {
            List<String> responsibilities = (workExp.getResponsibilities() == null || workExp.getResponsibilities().isEmpty())
                    ? generateResponsibilitiesUsingAI(request.getJobDescription(), workExp.getPosition(), workExp.getCompany())
                    : workExp.getResponsibilities();
            workExp.setResponsibilities(responsibilities);
            updatedWorkExperiences.add(workExp);
        }

        // Build response object
        ResumeAiResponse response = ResumeAiResponse.builder()
                .contactDetails(request.getContactDetails())
                .linkedinProfile(request.getLinkedinProfile())
                .portfolioLinks(request.getPortfolioLinks())
                .professionalSummary(aiGeneratedSummary)
                .skills(aiGeneratedSkills)
                .workExperience(updatedWorkExperiences)
                .education(request.getEducation())
                .relevantCourseWork(request.getRelevantCourseWork())
                .certifications(request.getCertifications())
                .reference(request.getReference())
                .build();

        ResumeAi resumeAi = resumeMapper.toModel(response);
        resumeRepository.save(resumeAi);
        return response;
    }

    /**
     * Calls AI to generate responsibilities based on job description & experience.
     */
    private List<String> generateResponsibilitiesUsingAI(String jobDescription, String position, String company) {
        String prompt = "You are an expert resume builder. Based on the job description:\n\n"
                + jobDescription + "\n\n"
                + "For a candidate who worked as " + position + " at " + company + ", "
                + "Create content based on the individual's actual experience and examples of achievements. " +
                "Ensure that the experience is tailored, humanized, quantifiable, and logically structured." +
                " Do not copy the job description directly; instead, provide a highly specific " +
                "list of job responsibilities in JSON array format. For example: "
                + "[\"Responsibility 1\", \"Responsibility 2\", \"Responsibility 3\"]";

        String aiResponse = callOpenAi(prompt);
        return List.of(aiResponse.split(","));
    }

    /**
     * Calls AI to generate a professional summary based on job description & experience.
     */
    private String generateSummaryUsingAI(String
                                                  jobDescription, List<ResumeAi.WorkExperience> workExperience) {
        String prompt = "You are an AI resume expert. Based on the job description below and the candidate's experience, "
                + "generate a compelling and humanly professional summary:\n\n"
                + "Job Description: " + jobDescription + "\n"
                + "Experience: " + workExperience + "\n"
                + "Write a strong and engaging professional summary in 3-4 sentences.";

        return callOpenAi(prompt);
    }

    /**
     * Calls AI to generate a list of relevant skills.
     */
    private List<String> generateSkillsUsingAI(String
                                                       jobDescription, List<ResumeAi.WorkExperience> workExperience) {
        String prompt = "You are an AI resume expert. Based on the job description and experience, "
                + "generate a list of the top 5 relevant skills:\n\n"
                + "Job Description: " + jobDescription + "\n"
                + "Experience: " + workExperience + "\n\n"
                + "Provide a JSON array of skills, e.g.: [\"Skill 1\", \"Skill 2\", \"Skill 3\"]";

        String aiResponse = callOpenAi(prompt);
        return List.of(aiResponse.split(",")); // Convert AI response to List
    }

    /**
     * Generic method to call OpenAI API and get response.
     */
    private String callOpenAi(String prompt) {
        OpenAiApi.ChatCompletionMessage message = new OpenAiApi.ChatCompletionMessage(prompt, OpenAiApi.ChatCompletionMessage.Role.USER);
        List<OpenAiApi.ChatCompletionMessage> messages = List.of(message);

        OpenAiApi.ChatCompletionRequest completionRequest = new OpenAiApi.ChatCompletionRequest(messages, "gpt-4", 0.3);
        ResponseEntity<OpenAiApi.ChatCompletion> responseEntity = openAiApi.chatCompletionEntity(completionRequest);
        OpenAiApi.ChatCompletion response = responseEntity.getBody();

        if (response != null && response.choices() != null && !response.choices().isEmpty()) {
            return response.choices().get(0).message().content().trim();
        }
        return "AI Generated Placeholder"; // Default fallback response
    }

    public ResumeAi saveResume(ResumeAi resumeAi) {
        Optional<ResumeAi> existingResume = resumeRepository.findByResumeId(resumeAi.getResumeId());

        if (existingResume.isPresent()) {
            ResumeAi existing = existingResume.get();
            existing.setContactDetails(resumeAi.getContactDetails());
            existing.setLinkedinProfile(resumeAi.getLinkedinProfile());
            existing.setPortfolioLinks(resumeAi.getPortfolioLinks());
            existing.setProfessionalSummary(resumeAi.getProfessionalSummary());
            existing.setSkills(resumeAi.getSkills());
            existing.setWorkExperience(resumeAi.getWorkExperience());
            existing.setEducation(resumeAi.getEducation());
            existing.setRelevantCourseWork(resumeAi.getRelevantCourseWork());
            existing.setCertifications(resumeAi.getCertifications());
            existing.setReference(resumeAi.getReference());
            return resumeRepository.save(existing); // Update existing record

        } else {
            // Save the new record if it doesn't exist
            return resumeRepository.save(resumeAi);
        }

    }

    public ResumeAiResponse getResumeById(String resumeId) {
        ResumeAi resumeAi = resumeRepository.findByResumeId(resumeId).orElseThrow(() ->
                new ResumeNotFoundException("Resume not found for : " + resumeId));

        return resumeMapper.toResponse(resumeAi);
    }

    public void deleteByResumeId (String resumeId) {
        if (!resumeRepository.existsById(resumeId)){
            throw new ResumeNotFoundException(" Resume with ID " + resumeId + " not found");
        }
        resumeRepository.deleteById(resumeId);
    }

    public ResumeAiResponse uploadResumeFile(MultipartFile file) throws IOException, TikaException {
        // Parse the uploaded file
        File tempFile = File.createTempFile("resume", null);
        file.transferTo(tempFile);


        String parsedText = ResumeParser.extractText(tempFile);

        // Step 3: Process extracted text with OpenAI GPT
        String aiJsonResponse = sendToOpenAi(parsedText);

        // Step 4: Convert AI-generated JSON into `ResumeAiResponse` DTO
        return objectMapper.readValue(aiJsonResponse, ResumeAiResponse.class);

    }

    private String sendToOpenAi(String extractedText) {
        String prompt = "Extract structured JSON information from this resume text:\n\n"
                + extractedText
                + "\n\nReturn JSON format with: {\"contactDetails\": {\"name\": \"\", \"email\": \"\", \"phone\": \"\"},"
                + "\"linkedinProfile\": \"\", \"portfolioLinks\": [], \"professionalSummary\": \"\", \"skills\": [],"
                + "\"workExperience\": [{\"company\": \"\", \"position\": \"\", \"years\": \"\"}],"
                + "\"education\": [{\"school\": \"\", \"degree\": \"\", \"year\": \"\"}],"
                + "\"relevantCourseWork\": \"\", \"certifications\": [{\"name\": \"\", \"year\": \"\"}],"
                + "\"reference\": [{\"name\": \"\", \"contact\": \"\"}]}";

        OpenAiApi.ChatCompletionMessage message = new OpenAiApi.ChatCompletionMessage(prompt, OpenAiApi.ChatCompletionMessage.Role.USER);
        List<OpenAiApi.ChatCompletionMessage> messages = List.of(message);

        OpenAiApi.ChatCompletionRequest request = new OpenAiApi.ChatCompletionRequest(messages, "gpt-4", 0.3);
        ResponseEntity<OpenAiApi.ChatCompletion> responseEntity = openAiApi.chatCompletionEntity(request);
        OpenAiApi.ChatCompletion response = responseEntity.getBody();

        if (response != null && response.choices() != null && !response.choices().isEmpty()) {
            return response.choices().get(0).message().content().trim();
        }
        return "{\"error\": \"Failed to generate structured data\"}";
    }

    public ResumeAiResponse updateResumeForJob(ResumeAiResponse currentResume, String jobDescription) throws IOException {
        // Convert the current resume object to a JSON string.
        String currentResumeJson = objectMapper.writeValueAsString(currentResume);
        // Log the current resume JSON for debugging.
        System.out.println("Current Resume JSON: " + currentResumeJson);

        // Build the prompt instructing OpenAI to update only selected fields.
        String prompt = "You are an expert resume optimizer. " +
                "Here is the current resume in JSON format:\n" + currentResumeJson +
                "\n\nThe job description is:\n" + jobDescription +
                "\n\nPlease update the professionalSummary, workExperience, and skills fields " +
                "to be tailored to the job description with improved, quantifiable, achievement, " +
                "be logical like humanmore detailed, and quantitative information. " +
                "Return strictly valid JSON using the exact same schema as the input.";
        System.out.println("Update Prompt: " + prompt);

        // Send this prompt to OpenAI and get the updated JSON.
        String aiUpdatedJson = passToOpenAi(prompt);
        System.out.println("AI Updated JSON: " + aiUpdatedJson);

        // Deserialize the JSON into the ResumeAiResponse DTO.
        ResumeAiResponse updatedResume = objectMapper.readValue(aiUpdatedJson, ResumeAiResponse.class);

        ResumeAi resumeAi = resumeMapper.toModel(updatedResume);

        resumeRepository.save(resumeAi);

        return updatedResume;
    }

    /**
     * Sends a prompt to OpenAI via API client and returns the output string.
     */
    private String passToOpenAi(String prompt) {
        // Build message for the prompt.
        OpenAiApi.ChatCompletionMessage message =
                new OpenAiApi.ChatCompletionMessage(prompt, OpenAiApi.ChatCompletionMessage.Role.USER);
        List<OpenAiApi.ChatCompletionMessage> messages = List.of(message);

        OpenAiApi.ChatCompletionRequest request =
                new OpenAiApi.ChatCompletionRequest(messages, "gpt-4", 0.3);

        ResponseEntity<OpenAiApi.ChatCompletion> responseEntity = openAiApi.chatCompletionEntity(request);
        OpenAiApi.ChatCompletion response = responseEntity.getBody();

        if (response != null && response.choices() != null && !response.choices().isEmpty()) {
            return response.choices().get(0).message().content().trim();
        }
        return "{\"error\": \"Failed to generate updated data.\"}";
    }
}



