package com.github.gyrosofwar.imagehive.service.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.gyrosofwar.imagehive.BaseTest;
import jakarta.inject.Inject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;
import org.apache.tika.mime.MimeTypeException;
import org.junit.jupiter.api.Test;

class OpenAiImageLabelerTest extends BaseTest {

  @Inject
  private OpenAiImageLabeler imageLabeler;

  @Test
  void testLabelImages() throws IOException, MimeTypeException {
    var image = new BufferedInputStream(
      Objects.requireNonNull(getClass().getResourceAsStream("/images/test-1.jpg"))
    );
    var description = imageLabeler.getDescription(image);
    assertThat(description).isNotBlank();
  }
}
