/*
 * Copyright 2024 Raonsecure
 */

package org.omnione.sender;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of different blockchain types supported by the application.
 * <p>
 * Currently, only Hyperledger Fabric is supported. This enum provides a way to represent and manage different types of
 * blockchain systems.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum BlockChainType {

  /**
   * Represents the Hyperledger Fabric blockchain type.
   */
  HYPER_LEDGER_FABRIC("fabric");

  private final String blockChainType;
}
