package com.github.gyrosofwar.imagehive.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.gyrosofwar.imagehive.BaseTest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class ImageControllerTest extends BaseTest {

  @Test
  void testCreateImage() throws IOException {
    var bytes = getClass().getResourceAsStream("/images/test-1.jpg").readAllBytes();
    var body = MultipartBody
      .builder()
      .addPart("file", "test-1.jpg", MediaType.IMAGE_JPEG_TYPE, bytes)
      .build();

    var token = appClient.login(new UsernamePasswordCredentials(username, password));
    var header = String.format("Bearer %s", token.getAccessToken());
    var response = appClient.uploadImage(body, header);
    assertEquals(200, response.code());
  }
}
