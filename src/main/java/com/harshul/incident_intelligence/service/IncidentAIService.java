package com.harshul.incident_intelligence.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class IncidentAIService {

    private final ChatClient chatClient;

    public IncidentAIService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String analyzeLog(String log) {

        String prompt = """
    You are an incident analysis system.

    Analyze the following error log and return STRICT JSON ONLY (no extra text):

    {
      "severity": "LOW | MEDIUM | HIGH",
      "rootCause": "short root cause",
      "confidence": number between 0 and 1
    }

    Log:
    """ + log;

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
