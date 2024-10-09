package com.github.gyrosofwar.imagehive.service.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ChatCompletionsResponse(
  String model,
  List<ChatCompletionChoice> choices,
  @JsonProperty("stop_reason") String stopReason
) {
  public record ChatCompletionChoice(ChatCompletionResponseMessage message) {}
  public record ChatCompletionResponseMessage(
    String content,
    ChatCompletionsRequest.MessageRole role
  ) {}
}
