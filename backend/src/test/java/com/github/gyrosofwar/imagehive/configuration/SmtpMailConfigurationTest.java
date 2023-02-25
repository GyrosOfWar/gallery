package com.github.gyrosofwar.imagehive.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.gyrosofwar.imagehive.BaseTest;
import io.micronaut.context.ApplicationContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

@MicronautTest
public class SmtpMailConfigurationTest extends BaseTest {

  @Test
  public void testSmtpMailConfiguration() {
    Map<String, Object> config = new HashMap<>();
    config.put("imagehive.image-base-path", "images");
    config.put("imagehive.smtpmail.host", "localhost");
    config.put("imagehive.smtpmail.port", "25");
    config.put("imagehive.smtpmail.auth", true);
    config.put("imagehive.smtpmail.username", "dummy");
    config.put("imagehive.smtpmail.password", "dummy");

    ApplicationContext ctx = ApplicationContext.run(config);
    SmtpMailConfiguration smtpMailConfiguration = ctx.getBean(SmtpMailConfiguration.class);

    assertEquals("localhost", smtpMailConfiguration.host());
    assertEquals("25", smtpMailConfiguration.port());
    assertTrue(smtpMailConfiguration.auth());
    assertEquals("dummy", smtpMailConfiguration.username());
    assertEquals("dummy", smtpMailConfiguration.password());
  }
}
