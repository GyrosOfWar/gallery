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
    var jdbc = new Jdbc();
    jdbc.setDriver("org.postgresql.Driver");
    jdbc.setUrl(System.getenv("IMAGEHIVE_PG_JDBC_URL"));
    jdbc.setUser(System.getenv("IMAGEHIVE_PG_USER"));
    jdbc.setPassword(System.getenv("IMAGEHIVE_PG_PASSWORD"));
    return jdbc;
  }

  private static Generator getGenerator() {
    var database = new Database();
    database.setName(PostgresDatabase.class.getName());
    database.setInputSchema("public");
    database.setIncludeExcludeColumns(true);
    database.setIncludes(".*");
    database.setExcludes("flyway_schema_history|ts_vector");

    var generate = new Generate();
    generate.setPojos(true);
    generate.setJavaTimeTypes(true);
    generate.setPojosAsJavaRecordClasses(true);
    generate.setPojosEqualsAndHashCode(false);
    generate.setPojosToString(false);
    generate.setRoutines(false);

    var target = new Target();
    target.setPackageName("com.github.gyrosofwar.imagehive.sql");
    target.setDirectory("../backend/src/main/java");


    var generator = new Generator();
    generator.setDatabase(database);
    generator.setGenerate(generate);
    generator.setTarget(target);

    return generator;
  }

  public static void main(String[] args) throws Exception {
    var configuration = new Configuration();
    configuration.setJdbc(getJdbc());
    configuration.setGenerator(getGenerator());

    GenerationTool.generate(configuration);
  }
}
