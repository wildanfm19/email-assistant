package com.wildan.email_assistant.service;

import com.wildan.email_assistant.dto.EmailRequest;
import org.springframework.stereotype.Service;

@Service
public class EmailGeneratorService {

    public String generateEmailReply(EmailRequest emailRequest){
        // Buat prompt
        String prompt = buildPrompt(emailRequest);
        // Buat Request

        // Lakuin Request ke geminiAPI dan dapetin response
        // return responsenya
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
