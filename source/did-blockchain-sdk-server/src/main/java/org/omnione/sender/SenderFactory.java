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

import java.util.Arrays;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.omnione.sender.fabric.FabricSender;

/**
 * Enum for creating instances of {@code OpenDidSender} based on the blockchain type.
 * <p>
 * This enum provides a way to instantiate the appropriate {@code OpenDidSender} implementation based on the specified
 * {@code BlockChainType}. Each enum constant is associated with a specific blockchain type and a supplier for creating
 * the corresponding sender instance.
 * </p>
 */
@RequiredArgsConstructor
public enum SenderFactory {

  /**
   * Represents the sender for Hyperledger Fabric blockchain.
   */
  FABRIC(BlockChainType.HYPER_LEDGER_FABRIC, FabricSender::new);

  final BlockChainType blockChainType;
  final Supplier<OpenDidSender> senderSupplier;

  /**
   * Gets the {@code OpenDidSender} instance for the specified blockchain type.
   * <p>
   * This method retrieves the appropriate sender factory based on the provided blockchain type and uses the associated
   * supplier to create and return the {@code OpenDidSender} instance.
   * </p>
   *
   * @param blockChainType the type of blockchain for which to get the sender instance
   * @return an instance of {@code OpenDidSender} corresponding to the specified blockchain typet
   * @throws IllegalArgumentException if no matching sender factory is found for the given blockchain type
   */
  public static OpenDidSender getSender(BlockChainType blockChainType) {
    SenderFactory senderFactory = Arrays.stream(SenderFactory.values())
        .filter(sender -> blockChainType == sender.blockChainType)
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);

    return senderFactory.senderSupplier.get();
  }
}
