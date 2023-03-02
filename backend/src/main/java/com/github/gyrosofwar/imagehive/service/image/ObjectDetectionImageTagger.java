package com.github.gyrosofwar.imagehive.service.image;

import ai.djl.Application;
import ai.djl.MalformedModelException;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class ObjectDetectionImageTagger implements ImageTagger {
  private DetectedObjects detectObjects(Image image) throws IOException {
    Criteria<Image, DetectedObjects> criteria = Criteria
      .builder()
      .optApplication(Application.CV.OBJECT_DETECTION)
      .setTypes(Image.class, DetectedObjects.class)
      .optFilter("backbone", "resnet50")
      .optEngine(Engine.getDefaultEngineName())
      .build();

    try (ZooModel<Image, DetectedObjects> model = criteria.loadModel()) {
      try (Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
        //saveBoundingBoxImage(img, detection);
        return predictor.predict(image);
      } catch (TranslateException e) {
        throw new RuntimeException(e);
      }
    } catch (ModelNotFoundException | MalformedModelException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Set<String> getTags(InputStream inputStream) {
    try {
      var objects = detectObjects(ImageFactory.getInstance().fromInputStream(inputStream));

      return objects
        .items()
        .stream()
        .filter(obj -> obj.getProbability() >= 0.95)
        .map(obj -> obj.getClassName())
        .collect(Collectors.toSet());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
