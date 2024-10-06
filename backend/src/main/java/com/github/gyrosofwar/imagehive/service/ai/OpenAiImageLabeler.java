package com.github.gyrosofwar.imagehive.service.ai;

import com.github.gyrosofwar.imagehive.configuration.ImageHiveConfiguration;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Set;

@Singleton
@Requires(property = "imagehive.ai.type", value = "openai")
public class OpenAiImageLabeler implements ImageLabeler {
    private static final Logger log = LoggerFactory.getLogger(OpenAiImageLabeler.class);

    private final ImageHiveConfiguration.AiServiceConfiguration config;
    private final HttpClient httpClient;

    public OpenAiImageLabeler(ImageHiveConfiguration imageHiveConfiguration, HttpClient httpClient) {
        this.config = imageHiveConfiguration.ai();
        this.httpClient = httpClient;
    }

    @Override
    public Set<String> getTags(Path path) throws IOException {
        String payload = createPayload(DEFAULT_TAGGING_QUESTION, path);
        String jsonArrayString = callApi(payload);
        return Set.of();
    }

    @Override
    public String getDescription(Path path) throws IOException {
        String payload = createPayload(DEFAULT_DESCRIPTION_QUESTION, path);
        return callApi(payload);
    }

    protected String createPayload(String messageContent, Path filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(filePath);
        String base64Image = Base64.getEncoder().encodeToString(fileBytes);
        return createPayload(messageContent, base64Image);
    }

    protected String createPayload(String messageContent, String base64Image) {
        JsonObject payload = new JsonObject();

        payload.addProperty("model", config.inferenceModel());
        JsonArray messages = new JsonArray();

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");

        JsonArray content = new JsonArray();

        JsonObject textContent = new JsonObject();
        textContent.addProperty("type", "text");
        textContent.addProperty("text", messageContent);
        content.add(textContent);

        JsonObject imageContent = new JsonObject();
        imageContent.addProperty("type", "image_url");
        JsonObject imageUrl = new JsonObject();
        imageUrl.addProperty("url", base64Image);
        imageContent.add("image_url", imageUrl);
        content.add(imageContent);

        message.add("content", content);
        messages.add(message);
        payload.add("messages", messages);

        Gson gson = new Gson();
        return gson.toJson(payload);
    }

    protected String callApi(String payload) {
        URI chatCompletionsUri = URI.create(config.openaiUrl() + "/v1/chat/completions");
        HttpRequest<String> request = HttpRequest.POST(chatCompletionsUri, payload).contentType(MediaType.APPLICATION_JSON_TYPE);
        httpClient.retrieve(request);
        var response = httpClient.toBlocking().exchange(request, String.class);
        HttpStatus status = response.getStatus();
        if (status.getCode() == 200) {
            return response.body();
        } else {
            log.error("Received non 200 status code from openAi API: {}, reason: {}", status.getCode(), status.getReason());
        }
        return "";
    }
}
