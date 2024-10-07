package com.github.gyrosofwar.imagehive.service.ai;

import com.github.gyrosofwar.imagehive.configuration.ImageHiveConfiguration;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Requires(property = "imagehive.ai.type", value = "openai")
public class OpenAiImageLabeler implements ImageLabeler {

  private static final Logger log = LoggerFactory.getLogger(OpenAiImageLabeler.class);

  private final ImageHiveConfiguration.AiServiceConfiguration config;
  private final OpenAiCompatibleApi api;

  public OpenAiImageLabeler(
    ImageHiveConfiguration imageHiveConfiguration,
    OpenAiCompatibleApi api
  ) {
    this.config = imageHiveConfiguration.ai();
    this.api = api;
  }

  @Override
  public Set<String> getTags(InputStream inputStream) throws IOException {
    return Set.of();
  }

  @Override
  public String getDescription(InputStream inputStream) throws IOException {
    var payload = new ChatCompletionsRequest(
      config.inferenceModel(),
      List.of(
        ChatCompletionsRequest.ChatCompletionMessage.systemMessage("You are a helpful assistant"),
        ChatCompletionsRequest.ChatCompletionMessage.assistantMessage("How can I help you?"),
        ChatCompletionsRequest.ChatCompletionMessage.userMessage(
          ImageLabeler.DEFAULT_DESCRIPTION_QUESTION
        ),
        ChatCompletionsRequest.ChatCompletionMessage.userImageMessage(inputStream)
      ),
      400
    );

    var response = api.getChatCompletions(payload);

    return response.choices().getFirst().message().content();
  }
}
