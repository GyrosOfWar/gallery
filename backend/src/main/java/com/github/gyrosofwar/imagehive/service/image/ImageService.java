package com.github.gyrosofwar.imagehive.service.image;

import static com.github.gyrosofwar.imagehive.sql.Tables.ALBUM_IMAGE;
import static com.github.gyrosofwar.imagehive.sql.Tables.IMAGE;

import com.github.gyrosofwar.imagehive.converter.ImageDetailsDTOConverter;
import com.github.gyrosofwar.imagehive.converter.ImageListDTOConverter;
import com.github.gyrosofwar.imagehive.dto.image.ImageDetailsDTO;
import com.github.gyrosofwar.imagehive.dto.image.ImageListDTO;
import com.github.gyrosofwar.imagehive.dto.image.ImageUpdateDTO;
import com.github.gyrosofwar.imagehive.helper.TaskHelper;
import com.github.gyrosofwar.imagehive.service.MediaService;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ImageService {

  private static final Logger log = LoggerFactory.getLogger(ImageService.class);

  private final DSLContext dsl;
  private final MediaService mediaService;
  private final ImageDetailsDTOConverter imageDetailsDTOConverter;
  private final ImageListDTOConverter imageListDTOConverter;
  private final ImageLabeler imageLabeler;

  public ImageService(
    DSLContext dsl,
    MediaService mediaService,
    ImageDetailsDTOConverter imageDetailsDTOConverter,
    ImageListDTOConverter imageListDTOConverter,
    ImageLabeler imageLabeler
  ) {
    this.dsl = dsl;
    this.mediaService = mediaService;
    this.imageDetailsDTOConverter = imageDetailsDTOConverter;
    this.imageListDTOConverter = imageListDTOConverter;
    this.imageLabeler = imageLabeler;
  }

  @Transactional
  public Image getByUuid(UUID uuid, Long userId) {
    return dsl
      .selectFrom(IMAGE)
      .where(IMAGE.ID.eq(uuid).and(IMAGE.OWNER_ID.eq(userId)))
      .fetchOneInto(Image.class);
  }

  public boolean isOwner(UUID imageId, Long userId) {
    if (userId == null) {
      return false;
    }

    var count = dsl
      .selectCount()
      .from(IMAGE)
      .where(IMAGE.ID.eq(imageId).and(IMAGE.OWNER_ID.eq(userId)))
      .fetchOne()
      .value1();

    return count != 0;
  }

  public Page<ImageListDTO> listImages(@Nullable String query, Pageable pageable, long userId) {
    var where = IMAGE.OWNER_ID.eq(userId);
    if (StringUtils.isNotBlank(query)) {
      where =
        where.and(DSL.condition("ts_vec @@ plainto_tsquery('english', {0})", DSL.inline(query)));
    }

    var images = dsl
      .selectFrom(IMAGE)
      .where(where)
      .orderBy(IMAGE.CAPTURED_ON.desc().nullsLast())
      .offset(pageable.getOffset())
      .limit(pageable.getSize())
      .fetchInto(Image.class)
      .stream()
      .map(imageListDTOConverter::convert)
      .toList();

    var count = dsl.selectCount().from(IMAGE).where(where).fetchOne().value1();
    return Page.of(images, pageable, count);
  }

  public void delete(UUID uuid, Long userId) {
    if (!isOwner(uuid, userId)) {
      return;
    }

    dsl.deleteFrom(ALBUM_IMAGE).where(ALBUM_IMAGE.IMAGE_ID.eq(uuid)).execute();

    var filePath = dsl
      .deleteFrom(IMAGE)
      .where(IMAGE.ID.eq(uuid))
      .returningResult(IMAGE.FILE_PATH)
      .fetchOne();
    if (filePath != null) {
      try {
        mediaService.delete(uuid, Path.of(filePath.value1()), userId);
      } catch (IOException e) {
        log.warn("failed to delete files for image " + uuid, e);
      }
    }
  }

  @Transactional
  public void update(ImageUpdateDTO imageUpdate, Long userId) {
    var update = dsl.updateQuery(IMAGE);
    update.addConditions(IMAGE.ID.eq(imageUpdate.uuid()).and(IMAGE.OWNER_ID.eq(userId)));

    if (imageUpdate.tags() != null) {
      update.addValue(IMAGE.TAGS, imageUpdate.tags());
    }

    if (imageUpdate.description() != null) {
      update.addValue(IMAGE.DESCRIPTION, imageUpdate.description());
    }

    if (imageUpdate.title() != null) {
      update.addValue(IMAGE.TITLE, imageUpdate.title());
    }

    update.execute();
  }

  @Transactional
  public String setGeneratedDescription(Image image) {
    if (image.description() == null) {
      try {
        var description = imageLabeler.getDescription(Path.of(image.filePath()));
        var updateCount = dsl
          .update(IMAGE)
          .set(IMAGE.DESCRIPTION, description)
          .where(IMAGE.ID.eq(image.id()))
          .execute();
        log.debug(
          "updating description on image {} to '{}' matched {} rows",
          image.id(),
          description,
          updateCount
        );
        return description;
      } catch (IOException e) {
        log.error("failed to generate description:", e);
      }
    }
    return null;
  }

  @Transactional
  public Set<String> setGeneratedTags(Image image) {
    if (image.tags() == null || image.tags().length == 0) {
      try {
        var tags = imageLabeler.getTags(Path.of(image.filePath()));
        var tagsArray = tags.toArray(String[]::new);
        dsl.update(IMAGE).set(IMAGE.TAGS, tagsArray).where(IMAGE.ID.eq(image.id())).execute();

        return tags;
      } catch (IOException e) {
        log.error("failed to generate tags", e);
        return null;
      }
    } else {
      return null;
    }
  }

  public void setGeneratedDataAsync(Image image) {
    log.info("generating tags and description for file {}", image.id());
    TaskHelper.runInBackground(() -> {
      setGeneratedDescription(image);
      setGeneratedTags(image);
    });
  }

  @Transactional
  public boolean existsByTitle(String title) {
    return dsl.selectCount().from(IMAGE).where(IMAGE.TITLE.eq(title)).fetchOne().component1() > 0;
  }

  @Transactional
  public ImageDetailsDTO toggleFavorite(UUID uuid, Long userId) {
    if (!isOwner(uuid, userId)) {
      return null;
    }
    var image = dsl
      .update(IMAGE)
      .set(IMAGE.FAVORITE, DSL.not(IMAGE.FAVORITE))
      .where(IMAGE.ID.eq(uuid))
      .returningResult()
      .fetchOneInto(Image.class);

    return imageDetailsDTOConverter.convert(image);
  }
}
