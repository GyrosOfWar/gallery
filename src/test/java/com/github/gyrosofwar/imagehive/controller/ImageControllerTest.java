package com.github.gyrosofwar.imagehive.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.gyrosofwar.imagehive.BaseTest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
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
    assertNotNull(response.body());

    var uuid = response.body().id();

    var imageResponse = appClient.getImageBytes(uuid, header);
    assertEquals(200, imageResponse.code());
    assertNotNull(imageResponse.body());

    var image = ImageIO.read(new ByteArrayInputStream(imageResponse.body()));
    assertEquals(image.getWidth(), response.body().width());
    assertEquals(image.getHeight(), response.body().height());
  }
}
