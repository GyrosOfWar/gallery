package com.github.gyrosofwar.imagehive.service;

import static org.junit.jupiter.api.Assertions.*;

import com.drew.imaging.ImageProcessingException;
import com.github.gyrosofwar.imagehive.BaseTest;
import com.github.gyrosofwar.imagehive.dto.CreateAlbumDTO;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@MicronautTest
class AlbumServiceTest extends BaseTest {

  @Inject
  AlbumService albumService;

  @Inject
  ImageService imageService;

  @Test
  void testGetAlbumWithImages() throws ImageProcessingException, IOException {
    final var imageCount = 3;

    List<UUID> images = new ArrayList<>();
    for (int i = 0; i < imageCount; i++) {
      var fileName = "test-" + (i + 1) + ".jpg";
      var inputSteam = getClass().getResourceAsStream("/images/" + fileName);
      var created = imageService.create(
        new NewImage(
          inputSteam,
          userId,
          fileName,
          "image/jpeg",
          "Image " + (i + 1),
          "description",
          List.of("vienna")
        )
      );
      images.add(created.id());
    }

    var album = albumService.createAlbum(
      new CreateAlbumDTO("test", userId, List.of("tag1", "tag2"), images)
    );
    assertNotNull(album);

    var dto = albumService.getAlbumWithImages(album.id(), userId);
    assertEquals(3, dto.images().size());
  }
}
