package com.github.gyrosofwar.imagehive.service.mail;

import io.micronaut.core.annotation.NonNull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface EmailService {
  void send(@NotNull @Valid Email email);
}
