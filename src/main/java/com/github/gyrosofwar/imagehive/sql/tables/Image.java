/*
 * This file is generated by jOOQ.
 */
package com.github.gyrosofwar.imagehive.sql.tables;

import com.github.gyrosofwar.imagehive.sql.Keys;
import com.github.gyrosofwar.imagehive.sql.Public;
import com.github.gyrosofwar.imagehive.sql.tables.records.ImageRecord;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.JSONB;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Image extends TableImpl<ImageRecord> {

  private static final long serialVersionUID = 1L;

  /**
   * The reference instance of <code>public.image</code>
   */
  public static final Image IMAGE = new Image();

  /**
   * The class holding records for this type
   */
  @Override
  public Class<ImageRecord> getRecordType() {
    return ImageRecord.class;
  }

  /**
   * The column <code>public.image.id</code>.
   */
  public final TableField<ImageRecord, UUID> ID = createField(
    DSL.name("id"),
    SQLDataType.UUID.nullable(false),
    this,
    ""
  );

  /**
   * The column <code>public.image.created_on</code>.
   */
  public final TableField<ImageRecord, OffsetDateTime> CREATED_ON = createField(
    DSL.name("created_on"),
    SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false),
    this,
    ""
  );

  /**
   * The column <code>public.image.owner_id</code>.
   */
  public final TableField<ImageRecord, Long> OWNER_ID = createField(
    DSL.name("owner_id"),
    SQLDataType.BIGINT,
    this,
    ""
  );

  /**
   * The column <code>public.image.height</code>.
   */
  public final TableField<ImageRecord, Integer> HEIGHT = createField(
    DSL.name("height"),
    SQLDataType.INTEGER.nullable(false),
    this,
    ""
  );

  /**
   * The column <code>public.image.width</code>.
   */
  public final TableField<ImageRecord, Integer> WIDTH = createField(
    DSL.name("width"),
    SQLDataType.INTEGER.nullable(false),
    this,
    ""
  );

  /**
   * @deprecated Unknown data type. Please define an explicit {@link
   * org.jooq.Binding} to specify how this type should be handled. Deprecation
   * can be turned off using {@literal <deprecationOnUnknownTypes/>} in your
   * code generator configuration.
   */
  @Deprecated
  public final TableField<ImageRecord, Object> GEO_COORDINATES = createField(
    DSL.name("geo_coordinates"),
    org.jooq.impl.DefaultDataType.getDefaultDataType("\"pg_catalog\".\"point\""),
    this,
    ""
  );

  /**
   * The column <code>public.image.metadata</code>.
   */
  public final TableField<ImageRecord, JSONB> METADATA = createField(
    DSL.name("metadata"),
    SQLDataType.JSONB.nullable(false),
    this,
    ""
  );

  /**
   * The column <code>public.image.tags</code>.
   */
  public final TableField<ImageRecord, String[]> TAGS = createField(
    DSL.name("tags"),
    SQLDataType.VARCHAR.getArrayDataType(),
    this,
    ""
  );

  /**
   * The column <code>public.image.file_path</code>.
   */
  public final TableField<ImageRecord, String> FILE_PATH = createField(
    DSL.name("file_path"),
    SQLDataType.VARCHAR.nullable(false),
    this,
    ""
  );

  private Image(Name alias, Table<ImageRecord> aliased) {
    this(alias, aliased, null);
  }

  private Image(Name alias, Table<ImageRecord> aliased, Field<?>[] parameters) {
    super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
  }

  /**
   * Create an aliased <code>public.image</code> table reference
   */
  public Image(String alias) {
    this(DSL.name(alias), IMAGE);
  }

  /**
   * Create an aliased <code>public.image</code> table reference
   */
  public Image(Name alias) {
    this(alias, IMAGE);
  }

  /**
   * Create a <code>public.image</code> table reference
   */
  public Image() {
    this(DSL.name("image"), null);
  }

  public <O extends Record> Image(Table<O> child, ForeignKey<O, ImageRecord> key) {
    super(child, key, IMAGE);
  }

  @Override
  public Schema getSchema() {
    return aliased() ? null : Public.PUBLIC;
  }

  @Override
  public UniqueKey<ImageRecord> getPrimaryKey() {
    return Keys.IMAGE_PKEY;
  }

  @Override
  public List<ForeignKey<ImageRecord, ?>> getReferences() {
    return Arrays.asList(Keys.IMAGE__IMAGE_OWNER_ID_FKEY);
  }

  private transient User _user;

  public User user() {
    if (_user == null) _user = new User(this, Keys.IMAGE__IMAGE_OWNER_ID_FKEY);

    return _user;
  }

  @Override
  public Image as(String alias) {
    return new Image(DSL.name(alias), this);
  }

  @Override
  public Image as(Name alias) {
    return new Image(alias, this);
  }

  /**
   * Rename this table
   */
  @Override
  public Image rename(String name) {
    return new Image(DSL.name(name), null);
  }

  /**
   * Rename this table
   */
  @Override
  public Image rename(Name name) {
    return new Image(name, null);
  }

  // -------------------------------------------------------------------------
  // Row9 type methods
  // -------------------------------------------------------------------------

  @Override
  public Row9<UUID, OffsetDateTime, Long, Integer, Integer, Object, JSONB, String[], String> fieldsRow() {
    return (Row9) super.fieldsRow();
  }
}
