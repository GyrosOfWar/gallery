package com.github.gyrosofwar.imagehive.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEmailService implements EmailService {

  private static final Logger log = LoggerFactory.getLogger(
    LoggingEmailService.class
  );
  private static final String LINE_SEPARATOR = System.getProperty(
    "line.separator"
  );

  @Override
  public void send(Email email) {
    StringBuilder sb = new StringBuilder();

    sb.append("to: ").append(email.recipient()).append(LINE_SEPARATOR);
    sb.append("cc: ").append(email.cc()).append(LINE_SEPARATOR);
    sb.append("bcc: ").append(email.bcc()).append(LINE_SEPARATOR);
    sb.append("subject: ").append(email.subject()).append(LINE_SEPARATOR);
    sb.append("html: ").append(email.htmlBody()).append(LINE_SEPARATOR);
    sb.append("text: ").append(email.textBody()).append(LINE_SEPARATOR);
    sb.append("from: ").append(email.from()).append(LINE_SEPARATOR);

    if (log.isInfoEnabled()) {
      log.info(sb.toString());
    }
  }
}
