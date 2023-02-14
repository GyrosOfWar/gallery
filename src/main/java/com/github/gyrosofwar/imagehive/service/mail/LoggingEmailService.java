package com.github.gyrosofwar.imagehive.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEmailService implements EmailService {

  private static final Logger log = LoggerFactory.getLogger(LoggingEmailService.class);
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  @Override
  public void send(Email email) {
    log.info("to: " + email.recipient() + LINE_SEPARATOR);
    log.info("cc: " + email.cc() + LINE_SEPARATOR);
    log.info("bcc: " + email.bcc() + LINE_SEPARATOR);
    log.info("subject: " + email.subject() + LINE_SEPARATOR);
    log.info("html: " + email.htmlBody() + LINE_SEPARATOR);
    log.info("text: " + email.textBody() + LINE_SEPARATOR);
    log.info("from: " + email.from() + LINE_SEPARATOR);
  }
}
