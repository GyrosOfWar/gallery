package com.github.gyrosofwar.imagehive.service.mail;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface EmailService {
  void send(@NotNull @Valid Email email);
}
