package com.wildan.email_assistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wildan.email_assistant.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.SystemProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailGeneratorService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

//    public String testAPI(){
//        return "url : " + geminiApiUrl + "\nkey : " + geminiApiKey;
//    }

    public String generateEmailReply(EmailRequest emailRequest){
        // Buat prompt
        String prompt = buildPrompt(emailRequest);
        // Buat Request
        Map<String ,Object> requestBody = Map.of(
                "contents" , new Object[]{
                        Map.of("parts", new Object[]{
                            Map.of("text",prompt)
                })
                }
        );
        // Lakuin Request ke geminiAPI dan dapetin response
        String response = webClient.post()
                .uri(geminiApiUrl + "?key=" + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        // return extract response and return
        return extractResponseContent(response);

    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e){
            return "Error processing request: " + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a profesional email reply for the following email content. Please dont generate a subject line ");
        if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()){
            prompt.append("Use a").append(emailRequest.getTone()).append(" tone.");
        }

        prompt.append("/n Original email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();

    }
}
