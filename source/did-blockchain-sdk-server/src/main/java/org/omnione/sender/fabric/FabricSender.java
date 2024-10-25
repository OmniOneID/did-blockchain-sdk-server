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

import org.omnione.exception.BlockChainException;
import org.omnione.exception.BlockchainErrorCode;
import org.omnione.sender.ContractData;
import org.omnione.sender.OpenDidSender;
import org.omnione.sender.ServerInformation;
import java.util.concurrent.TimeoutException;
import lombok.NoArgsConstructor;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;

/**
 * The {@code FabricSender} class implements the {@link OpenDidSender} interface and is responsible
 * for sending transactions to a Hyperledger Fabric network.
 *
 * <p>This class handles both query transactions (using {@code sendEvaluateTransaction}) and
 * submit transactions (using {@code sendSubmitTransaction}), depending on the type of
 * operation specified in the {@link FabricContractData} object.</p>
 */
@NoArgsConstructor
public class FabricSender implements OpenDidSender {

  /**
   * Sends a transaction to the Hyperledger Fabric network.
   *
   * <p>This method determines whether the transaction is a query or submit operation and
   * delegates the request accordingly. After the transaction is processed, the method
   * returns the gateway connection back to the pool if it is not null.</p>
   *
   * @param serverInformation contains the connection details for the Fabric network
   * @param data              the contract data, including the function name and arguments
   * @return the result of the transaction as a byte array
   * @throws BlockChainException if there is an error during the transaction
   */
  @Override
  public byte[] sendTransaction(ServerInformation serverInformation, ContractData data) throws BlockChainException {
    FabricServerInformation fabricServerInformation = (FabricServerInformation) serverInformation;
    FabricContractData fabricContractData = (FabricContractData) data;
    Gateway gateway = null;
    gateway = fabricServerInformation.getGateway();
    Contract contract = gateway
        .getNetwork(fabricServerInformation.getNetworkName())
        .getContract(fabricServerInformation.getChaincodeName());

    byte[] result = fabricContractData.getIsQuery() ?
        sendEvaluateTransaction(contract, fabricContractData) : sendSubmitTransaction(contract, fabricContractData);
    if (gateway != null) {
      fabricServerInformation.returnGateway(gateway);
    }
    return result;
  }

  /**
   * Sends an evaluate transaction (query) to the Hyperledger Fabric network.
   *
   * <p>This method creates a transaction based on the function name and arguments provided in the
   * {@link FabricContractData} object, and then evaluates it using the Fabric contract. If the transaction
   * fails, a {@link BlockChainException} is thrown.</p>
   *
   * @param contract     the Fabric contract to be used for the transaction
   * @param contractData the data containing the function name and arguments for the transaction
   * @return the result of the evaluate transaction as a byte array
   * @throws BlockChainException if there is an error during the transaction
   */
  private byte[] sendEvaluateTransaction(Contract contract, FabricContractData contractData) throws BlockChainException {
    try {
      return contract.createTransaction(contractData.getFunctionName())
          .evaluate(contractData.getArguments().toArray(new String[]{}));
    } catch (ContractException e) {
      throw new BlockChainException(BlockchainErrorCode.TRANSACTION_ERROR, e);
    }
  }

  /**
   * Sends a submit transaction (invoke) to the Hyperledger Fabric network.
   *
   * <p>This method creates a transaction based on the function name and arguments provided in the
   * {@link FabricContractData} object, and then submits it using the Fabric contract. If the transaction
   * fails, a {@link BlockChainException} is thrown.</p>
   *
   * @param contract     the Fabric contract to be used for the transaction
   * @param contractData the data containing the function name and arguments for the transaction
   * @return the result of the submit transaction as a byte array
   * @throws BlockChainException if there is an error during the transaction, including contract exceptions,
   *                             timeout, or interruption
   */
  private byte[] sendSubmitTransaction(Contract contract, FabricContractData contractData) throws BlockChainException {
    try {
      return contract.createTransaction(contractData.getFunctionName())
          .submit(contractData.getArguments().toArray(new String[]{}));
    } catch (ContractException | TimeoutException | InterruptedException e) {
      throw new BlockChainException(BlockchainErrorCode.TRANSACTION_ERROR, e);
    }
  }
}
