package com.chatbot.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public class ChatService {

    private final DynamoDbClient dynamoDbClient;

    private final String TABLE_NAME = "Chatbotqa";
    private final String PARTITION_KEY = "keyword";
    private final String VALUE_KEY = "answer";

    public ChatService() {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_2) // <-- Set your DynamoDB region here
                .build();
    }

    public String getResponse(String keywordInput) {
        String keyword = keywordInput.toLowerCase().trim();
        System.out.println("🔍 Querying keyword: " + keyword);

        try {
            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":keywordVal", AttributeValue.builder().s(keyword).build());

            QueryRequest queryRequest = QueryRequest.builder()
                    .tableName(TABLE_NAME)
                    .keyConditionExpression(PARTITION_KEY + " = :keywordVal")
                    .expressionAttributeValues(expressionValues)
                    .limit(1)
                    .build();

            QueryResponse queryResponse = dynamoDbClient.query(queryRequest);
            System.out.println("📥 DynamoDB Response: " + queryResponse);

            if (!queryResponse.items().isEmpty()) {
                Map<String, AttributeValue> item = queryResponse.items().get(0);
                return item.containsKey(VALUE_KEY)
                        ? item.get(VALUE_KEY).s()
                        : "❗ Found the keyword, but no answer field exists.";
            } else {
                System.out.println("❌ No match found for keyword: " + keyword);
                return "Sorry, I couldn't find an answer for that. Try a different keyword!";
            }

        } catch (DynamoDbException e) {
            e.printStackTrace();
            return "An error occurred while fetching the answer.";
        }
    }
}
