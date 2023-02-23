package com.github.gyrosofwar.imagehive.service;

import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.BaseTest;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.github.gyrosofwar.imagehive.sql.Tables.IMAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class SearchServiceTest extends BaseTest {
  @Inject
  SearchService searchService;
  @Test
  public void testFindImages() {
    for (Image i : createTestImageData()) {
      dsl.newRecord(IMAGE, i).insert();
    }

    assertEquals(3, searchService.findImages("Testtitle").size());
    assertEquals(2, searchService.findImages("Tag3").size());
    assertEquals(1, searchService.findImages("description two").size());
  }

  private List<Image> createTestImageData() {
    List<Image> images = new ArrayList<>();

    images.add(new Image(
      Ulid.fast().toUuid(),
      "Testtitle One ",
      "This is test description two",
      OffsetDateTime.now(),
      1L,
      0,
      0,
      0.0,
      0.0,
      null,
      new String[] {"Tag1","Tag2","Tag3","Tag4","Tag5"},
      ""
    ));

    images.add(new Image(
      Ulid.fast().toUuid(),
      "Testtitle Two",
      "This is test description two",
      OffsetDateTime.now(),
      1L,
      0,
      0,
      0.0,
      0.0,
      null,
      new String[] {"Tag1","Tag3","Tag4"},
      ""
    ));

    images.add(new Image(
      Ulid.fast().toUuid(),
      "Testtitle Three",
      "This is test description three",
      OffsetDateTime.now(),
      1L,
      0,
      0,
      0.0,
      0.0,
      null,
      new String[] {"Tag7","Tag8","Tag9"},
      ""
    ));

    return images;
  }
}
