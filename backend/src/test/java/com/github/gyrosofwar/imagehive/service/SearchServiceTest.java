package com.github.gyrosofwar.imagehive.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.drew.imaging.ImageProcessingException;
import com.github.gyrosofwar.imagehive.BaseTest;
import com.github.gyrosofwar.imagehive.service.image.ImageCreationService;
import com.github.gyrosofwar.imagehive.service.image.NewImage;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

class SearchServiceTest extends BaseTest {

  @Inject
  SearchService searchService;

  @Inject
  ImageCreationService imageCreationService;

  @Test
  void testFindImages() throws ImageProcessingException, IOException {
    for (var image : createTestImageData()) {
      imageCreationService.create(image);
    }

    assertEquals(3, searchService.searchImages("Testtitle", Pageable.UNPAGED, 1).size());
    assertEquals(2, searchService.searchImages("Tag3", Pageable.UNPAGED, 1).size());
    assertEquals(1, searchService.searchImages("description two", Pageable.UNPAGED, 1).size());
  }

  private List<NewImage> createTestImageData() {
    List<NewImage> images = new ArrayList<>();

    images.add(
      new NewImage(
        getClass().getResourceAsStream("/images/image-1.jpg"),
        userId,
        "image-1.jpg",
        "image/jpeg",
        "Testtitle One ",
        "This is test description two",
        List.of("Tag1", "Tag2", "Tag3", "Tag4", "Tag5"),
        false
      )
    );

    images.add(
      new NewImage(
        getClass().getResourceAsStream("/images/image-2.jpg"),
        userId,
        "image-3.jpg",
        "image/jpg",
        "Testtitle Two",
        "This is test description two",
        List.of("Tag1", "Tag3", "Tag4"),
        false
      )
    );

    images.add(
      new NewImage(
        getClass().getResourceAsStream("/images/image-3.jpg"),
        userId,
        "image-3.jpg",
        "image/jpeg",
        "Testtitle Three",
        "This is test description three",
        List.of("Tag7", "Tag8", "Tag9"),
        false
      )
    );

    return images;
  }
}
