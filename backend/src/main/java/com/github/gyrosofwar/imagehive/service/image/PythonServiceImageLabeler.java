package com.github.gyrosofwar.imagehive.service.image;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.multipart.MultipartBody;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Singleton
@Requires("${imagehive.ai.enabled}")
public class PythonServiceImageLabeler implements ImageLabeler {

  private final AiServiceClient aiServiceClient;

  public PythonServiceImageLabeler(AiServiceClient aiServiceClient) {
    this.aiServiceClient = aiServiceClient;
  }

  @Override
  public Set<String> getTags(Path path) throws IOException {
    try (var inputStream = Files.newInputStream(path)) {
      var contentLength = Files.size(path);
      var body = MultipartBody.builder()
        .addPart(
          "images",
          "image.jpeg",
          MediaType.APPLICATION_OCTET_STREAM_TYPE,
          inputStream,
          contentLength
        )
        .build();

      var response = aiServiceClient.getTags(body);
      return Set.copyOf(response.tags().get(0));
    }
  }

  @Override
  public String getDescription(Path path) throws IOException {
    try (var inputStream = Files.newInputStream(path)) {
      var contentLength = Files.size(path);
      var body = MultipartBody.builder()
        .addPart(
          "images",
          "image.jpeg",
          MediaType.APPLICATION_OCTET_STREAM_TYPE,
          inputStream,
          contentLength
        )
        .build();

      return aiServiceClient.getCaption(body).captions().get(0);
    }
  }
}
