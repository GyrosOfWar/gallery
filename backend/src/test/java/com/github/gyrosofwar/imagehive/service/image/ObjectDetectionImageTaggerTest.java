package com.github.gyrosofwar.imagehive.service.image;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class ObjectDetectionImageTaggerTest {

  @Test
  void testDetectStuff() throws IOException {
    var objectDetector = new ObjectDetectionImageTagger();
    var image = getClass().getResourceAsStream("/images/test-4.jpg");
    var tags = objectDetector.getTags(image);
    System.out.println(tags);
  }
}
