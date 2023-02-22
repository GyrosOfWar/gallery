package com.github.gyrosofwar.imagehive.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.BaseTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

class MediaServiceTest extends BaseTest {

  @Inject
  MediaService mediaService;

  @Test
  void testCreatePath() {
    var ulid = Ulid.from("01GSFVJK09PXYNX26VWY488986");
    var result = mediaService.getImagePath(ulid, ".jpg", 1L);
    assertThat(result).hasFileName(ulid.toString() + ".jpg");
  }
}
