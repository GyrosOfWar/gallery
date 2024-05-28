package com.github.gyrosofwar.imagehive.codegen;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.jooq.meta.postgres.PostgresDatabase;

public class JooqGenerator {

  private static Jdbc getJdbc() {
    return new Jdbc()
      .withDriver("org.postgresql.Driver")
      .withUrl(System.getenv("IMAGEHIVE_PG_JDBC_URL"))
      .withUser(System.getenv("IMAGEHIVE_PG_USER"))
      .withPassword(System.getenv("IMAGEHIVE_PG_PASSWORD"));
  }

  private static Generator getGenerator() {
    var database = new Database()
      .withName(PostgresDatabase.class.getName())
      .withInputSchema("public")
      .withIncludeExcludeColumns(true)
      .withIncludes(".*")
      .withExcludes("flyway_schema_history|ts_vec");

    var generate = new Generate()
      .withPojos(true)
      .withJavaTimeTypes(true)
      .withPojosAsJavaRecordClasses(true)
      .withPojosEqualsAndHashCode(false)
      .withPojosToString(false)
      .withRoutines(false);

    var target = new Target()
      .withPackageName("com.github.gyrosofwar.imagehive.sql")
      .withDirectory("../backend/src/main/java");

    return new Generator().withDatabase(database).withGenerate(generate).withTarget(target);
  }

  public static void main(String[] args) throws Exception {
    var configuration = new Configuration();
    configuration.setJdbc(getJdbc());
    configuration.setGenerator(getGenerator());

    GenerationTool.generate(configuration);
  }
}
