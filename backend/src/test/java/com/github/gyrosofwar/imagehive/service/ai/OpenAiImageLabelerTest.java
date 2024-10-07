package com.github.gyrosofwar.imagehive.service.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.github.gyrosofwar.imagehive.BaseTest;
import jakarta.inject.Inject;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class OpenAiImageLabelerTest extends BaseTest {

  @Inject
  private OpenAiImageLabeler imageLabeler;

  @Test
  void testLabelImages() throws IOException {
    var image = getClass().getResourceAsStream("/images/test-1.jpg");
    var description = imageLabeler.getDescription(image);
    assertThat(description).isNotBlank();
  }
}
