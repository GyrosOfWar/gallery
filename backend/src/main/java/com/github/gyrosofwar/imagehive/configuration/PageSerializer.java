package com.github.gyrosofwar.imagehive.configuration;

import io.micronaut.core.type.Argument;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.serde.Encoder;
import io.micronaut.serde.Serializer;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class PageSerializer implements Serializer<Page<Object>> {

  @Override
  public void serialize(
    Encoder encoder,
    EncoderContext context,
    Argument<? extends Page<Object>> type,
    Page<Object> page
  ) throws IOException {
    Encoder e = encoder.encodeObject(type);

    e.encodeKey("content");
    Argument<List<Object>> contentType = Argument.listOf(
      (Argument<Object>) type.getFirstTypeVariable().orElse(Argument.OBJECT_ARGUMENT)
    );
    context
      .findSerializer(contentType)
      .createSpecific(context, contentType)
      .serialize(e, context, contentType, page.getContent());

    e.encodeKey("pageable");
    Argument<Pageable> pageable = Argument.of(Pageable.class);
    context
      .findSerializer(pageable)
      .createSpecific(context, pageable)
      .serialize(e, context, pageable, page.getPageable());

    e.encodeKey("totalSize");
    e.encodeLong(page.getTotalSize());

    e.finishStructure();
  }
}
