package com.chatbot.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.chatbot.service.ChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class ChatbotLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final ChatService chatService = new ChatService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String userInput = "unknown"; // Default if parsing fails

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode bodyJson = objectMapper.readTree(request.getBody());
            if (bodyJson.has("message")) {
                userInput = bodyJson.get("message").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String botReply = chatService.getResponse(userInput);

        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*"); // Important for CORS
        headers.put("Access-Control-Allow-Methods", "POST,OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type");

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withHeaders(headers)
                .withBody(botReply);
    }
}
