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

package org.omnione.sender.fabric;

import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.GatewayException;
import org.omnione.exception.BlockChainException;
import org.omnione.exception.BlockchainErrorCode;
import org.omnione.sender.ContractData;
import org.omnione.sender.OpenDidSender;
import org.omnione.sender.ServerInformation;
import lombok.NoArgsConstructor;

/**
 * The {@code FabricSender} class implements the {@link OpenDidSender} interface and is responsible
 * for sending transactions to a Hyperledger Fabric network.
 *
 * <p>This class handles both query transactions (using {@code sendEvaluateTransaction}) and submit
 * transactions (using {@code sendSubmitTransaction}), depending on the type of operation specified
 * in the {@link FabricContractData} object.
 */
@NoArgsConstructor
public class FabricSender implements OpenDidSender {

  /**
   * Sends a transaction to the Hyperledger Fabric network.
   *
   * <p>This method determines whether the transaction is a query or submit operation and delegates
   * the request accordingly. After the transaction is processed, the method returns the gateway
   * connection back to the pool if it is not null.
   *
   * @param serverInformation contains the connection details for the Fabric network
   * @param data the contract data, including the function name and arguments
   * @return the result of the transaction as a byte array
   * @throws BlockChainException if there is an error during the transaction
   */
  @Override
  public byte[] sendTransaction(ServerInformation serverInformation, ContractData data)
      throws BlockChainException {
    FabricServerInformation fabricServerInformation = (FabricServerInformation) serverInformation;
    FabricContractData fabricContractData = (FabricContractData) data;
    Gateway gateway = null;
    gateway = fabricServerInformation.getGateway();
    Contract contract =
        gateway
            .getNetwork(fabricServerInformation.getNetworkName())
            .getContract(fabricServerInformation.getChaincodeName());

    try {

      byte[] result =
          fabricContractData.getIsQuery()
              ? contract.evaluateTransaction(
                  fabricContractData.getFunctionName(),
                  fabricContractData.getArguments().toArray(new String[] {}))
              : contract.submitTransaction(
                  fabricContractData.getFunctionName(),
                  fabricContractData.getArguments().toArray(new String[] {}));
      fabricServerInformation.returnGateway(gateway);
      return result;
    } catch (Exception e) {
      fabricServerInformation.returnGateway(gateway);
      throw new BlockChainException(BlockchainErrorCode.TRANSACTION_ERROR, e);
    }
  }
}
