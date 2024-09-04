/*
 * Copyright 2024 Raonsecure
 */

package org.omnione.sender;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Abstract base class for storing and managing server information.
 */
@Getter
@Setter
@RequiredArgsConstructor
public abstract class ServerInformation {

  private String host;
  private int port;
}
