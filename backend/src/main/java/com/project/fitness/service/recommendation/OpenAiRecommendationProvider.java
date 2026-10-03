package com.project.fitness.service.recommendation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fitness.model.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OpenAiRecommendationProvider implements RecommendationProvider {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.openai.api-key}")
    private String apiKey;
    @Value("${app.openai.model}")
    private String model;

    @Override
    public RecommendationAdvice generate(Activity activity) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI recommendations are not configured");
        }

        String exercise = "Activity: " + activity.getType()
                + "; duration minutes: " + activity.getDuration()
                + "; calories: " + activity.getCaloriesBurned()
                + "; metrics: " + activity.getAdditionalMetrics();
        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "type", Map.of("type", "string"),
                        "recommendation", Map.of("type", "string"),
                        "improvements", Map.of("type", "array", "items", Map.of("type", "string")),
                        "suggestions", Map.of("type", "array", "items", Map.of("type", "string")),
                        "safety", Map.of("type", "array", "items", Map.of("type", "string"))
                ),
                "required", List.of("type", "recommendation", "improvements", "suggestions", "safety"),
                "additionalProperties", false
        );
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "store", false,
                "instructions", "Give cautious, general fitness coaching based only on the supplied activity. Do not diagnose or prescribe treatment. Include a concise safety note advising the user to stop if they feel pain and consult a qualified professional when appropriate. Return only the requested structured fields.",
                "input", exercise,
                "text", Map.of("format", Map.of("type", "json_schema", "name", "fitness_recommendation", "strict", true, "schema", schema))
        );

        try {
            JsonNode response = RestClient.builder().baseUrl("https://api.openai.com/v1")
                    .defaultHeader("Authorization", "Bearer " + apiKey)
                    .build()
                    .post().uri("/responses").contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody).retrieve().body(JsonNode.class);
            if (response == null) throw new IllegalStateException("The AI provider returned an empty response");
            String text = response.path("output").findValuesAsText("text").stream().collect(Collectors.joining());
            JsonNode advice = objectMapper.readTree(text);
            return new RecommendationAdvice(
                    advice.path("type").asText("Training"),
                    advice.path("recommendation").asText(),
                    strings(advice.path("improvements")),
                    strings(advice.path("suggestions")),
                    strings(advice.path("safety"))
            );
        } catch (RestClientException | java.io.IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Recommendation provider is temporarily unavailable");
        }
    }

    private List<String> strings(JsonNode node) {
        if (!node.isArray()) return List.of();
        return objectMapper.convertValue(node, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
    }
}
