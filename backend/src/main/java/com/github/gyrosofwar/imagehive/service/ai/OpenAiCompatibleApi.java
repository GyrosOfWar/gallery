package com.github.gyrosofwar.imagehive.service.ai;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;

@Singleton
@Requires(property = "imagehive.ai.type", value = "openai")
@Client("http://localhost:11434")
public interface OpenAiCompatibleApi {
  @Post("/v1/chat/completions")
  ChatCompletionsResponse getChatCompletions(@Body ChatCompletionsRequest request);
}
