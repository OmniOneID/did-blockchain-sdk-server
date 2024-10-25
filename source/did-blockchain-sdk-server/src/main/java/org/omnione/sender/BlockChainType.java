/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
