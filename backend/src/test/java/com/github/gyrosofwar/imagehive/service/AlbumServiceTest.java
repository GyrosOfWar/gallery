package com.github.gyrosofwar.imagehive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.drew.imaging.ImageProcessingException;
import com.github.gyrosofwar.imagehive.BaseTest;
import com.github.gyrosofwar.imagehive.dto.album.CreateAlbumDTO;
import com.github.gyrosofwar.imagehive.service.image.ImageCreationService;
import com.github.gyrosofwar.imagehive.service.image.NewImage;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AlbumServiceTest extends BaseTest {

  @Inject
  AlbumService albumService;

  @Inject
  ImageCreationService imageService;

  @Test
  void testGetAlbum() {
    var album = albumService.createAlbum(
      new CreateAlbumDTO("test", "cool description", List.of("tag1", "tag2")),
      userId
    );
    assertNotNull(album);

    var dto = albumService.getAlbum(album.id(), userId);
    assertEquals("test", dto.name());
    assertEquals("cool description", dto.description());
    assertEquals(List.of("tag1", "tag2"), dto.tags());
  }

  @Test
  void testGetAlbumWithImages() throws ImageProcessingException, IOException {
    final var imageCount = 3;
    assertNotNull(userId);

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
      new CreateAlbumDTO("test", "cool description", List.of("tag1", "tag2")),
      userId
    );
    assertNotNull(album);

    var dto = albumService.getAlbum(album.id(), userId);
    assertEquals("test", dto.name());
    assertEquals("cool description", dto.description());
    assertEquals(List.of("tag1", "tag2"), dto.tags());

    albumService.updateAlbumImages(dto.id(), Set.copyOf(images), userId);

    var result = albumService.getImages(album.id(), userId);
    assertThat(result).hasSize(imageCount);
  }
}
