/*
 * Copyright 2024 Raonsecure
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
