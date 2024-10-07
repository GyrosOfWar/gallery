package com.github.gyrosofwar.imagehive.service.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;

public record ChatCompletionsRequest(
  String model,
  List<ChatCompletionMessage> messages,
  @JsonProperty("max_tokens") int maxTokens
) {
  public record ChatCompletionMessage(MessageRole role, List<MessageContent> content) {
    public static ChatCompletionMessage systemMessage(String prompt) {
      return new ChatCompletionMessage(
        MessageRole.System,
        List.of(new MessageContent(MessageContentType.Text, prompt, null))
      );
    }

    public static ChatCompletionMessage assistantMessage(String prompt) {
      return new ChatCompletionMessage(
        MessageRole.Assistant,
        List.of(new MessageContent(MessageContentType.Text, prompt, null))
      );
    }

    public static ChatCompletionMessage userMessage(String prompt) {
      return new ChatCompletionMessage(
        MessageRole.User,
        List.of(new MessageContent(MessageContentType.Text, prompt, null))
      );
    }

    private static BufferedImage resizeImage(InputStream inputStream) throws IOException {
      final int maxWidth = 1400, maxHeight = 1400;

      var image = ImageIO.read(inputStream);
      if (image.getWidth() < maxWidth && image.getHeight() < maxHeight) {
        return image;
      } else {
        return Thumbnails.of(image).size(maxWidth, maxHeight).asBufferedImage();
      }
    }

    public static ChatCompletionMessage userImageMessage(InputStream inputStream)
      throws IOException {
      var scaledDownImage = resizeImage(inputStream);
      var stream = new ByteArrayOutputStream(1024 * 1024);

      ImageIO.write(scaledDownImage, "jpeg", stream);
      var base64 = Base64.getEncoder().encodeToString(stream.toByteArray());
      var url = String.format("data:image/jpeg;base64,%s", base64);

      return new ChatCompletionMessage(
        MessageRole.User,
        List.of(new MessageContent(MessageContentType.ImageUrl, null, url))
      );
    }
  }

  public record MessageContent(
    MessageContentType type,
    String text,
    @JsonProperty("image_url") String imageUrl
  ) {}

  public enum MessageRole {
    @JsonProperty("system")
    System,
    @JsonProperty("user")
    User,
    @JsonProperty("assistant")
    Assistant,
  }

  public enum MessageContentType {
    @JsonProperty("text")
    Text,
    @JsonProperty("image_url")
    ImageUrl,
  }
}
