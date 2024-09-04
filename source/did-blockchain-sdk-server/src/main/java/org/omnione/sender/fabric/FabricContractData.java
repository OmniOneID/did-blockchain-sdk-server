/*
 * Copyright 2024 Raonsecure
 */

package org.omnione.sender.fabric;

import org.omnione.sender.ContractData;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the data required to execute a contract function in Hyperledger Fabric.
 * <p>
 * This class encapsulates the function name, arguments, and query flag needed to invoke or query a smart contract in a
 * Hyperledger Fabric network. It provides static factory methods to create instances for queries and invocations.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public class FabricContractData extends ContractData {

  private final String functionName;
  private final List<String> arguments;
  private final Boolean isQuery;


  /**
   * Creates a {@code FabricContractData} instance for a query.
   *
   * @param functionName the function name to be called
   * @param arguments    the arguments to be passed to the function
   * @return new instance of {@code FabricContractData} for invoke
   */
  public static FabricContractData Query(FunctionName functionName, String... arguments) {
    return new FabricContractData(functionName.getFunctionName(), List.of(arguments), true);
  }

  /**
   * Creates a {@code FabricContractData} instance for an invocation.
   *
   * @param functionName the function name to be called
   * @param arguments    the arguments to be passed to the function
   * @return new instance of {@code FabricContractData} for query
   */
  public static FabricContractData Invoke(FunctionName functionName, String... arguments) {
    return new FabricContractData(functionName.getFunctionName(), List.of(arguments), false);
  }
}
