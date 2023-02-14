package com.github.gyrosofwar.imagehive.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEmailService implements EmailService {

  private static final Logger log = LoggerFactory.getLogger(LoggingEmailService.class);

  @Override
  public void send(Email email) {
    StringBuilder sb = new StringBuilder();

    sb.append("to: ").append(email.recipient()).append(System.getProperty("line.separator"));
    sb.append("cc: ").append(email.cc()).append(System.getProperty("line.separator"));
    sb.append("bcc: ").append(email.bcc()).append(System.getProperty("line.separator"));
    sb.append("subject: ").append(email.subject()).append(System.getProperty("line.separator"));
    sb.append("html: ").append(email.htmlBody()).append(System.getProperty("line.separator"));
    sb.append("text: ").append(email.textBody()).append(System.getProperty("line.separator"));
    sb.append("from: ").append(email.from()).append(System.getProperty("line.separator"));

    log.info(sb.toString());
  }
}
