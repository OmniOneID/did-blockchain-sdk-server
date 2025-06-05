package org.omnione.did.ethereum;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import org.omnione.did.ContractApi;
import org.omnione.did.crypto.util.MultiBaseUtils;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.exception.BlockChainException;
import org.omnione.exception.BlockchainErrorCode;
import org.omnione.generated.OpenDID;
import org.omnione.generated.OpenDID.CredentialDefinition;
import org.omnione.response.EvmResponse;
import org.omnione.sender.ethereum.EvmContractData;
import org.omnione.sender.ethereum.EvmServerInformation;
import org.omnione.util.DidKeyUrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.exceptions.ContractCallException;
import org.web3j.tx.gas.StaticGasProvider;
import okhttp3.OkHttpClient;

public class EvmContractApi implements ContractApi {

  private final Logger logger = LoggerFactory.getLogger(EvmContractApi.class);
  private final EvmServerInformation serverInformation;
  private final EvmContractData contractData;

  /**
   * Constructor to initialize server and contract information.
   *
   * @param resourcePath Path to the resource file containing configuration.
   * @throws IOException If there is an error reading the resource file.
   */
  public EvmContractApi(String resourcePath) throws IOException {
    logger.info(
        "Initializing EvmContractApi with resource path: {}",
        resourcePath
    );

    this.serverInformation = new EvmServerInformation(resourcePath);
    this.contractData = new EvmContractData(resourcePath);
  }

  /**
   * Creates a Web3j instance for interacting with the Ethereum network.
   *
   * @return Web3j instance.
   */
  private Web3j createWeb3j(OkHttpClient httpClient) {
    logger.debug(
        "Creating Web3j instance for network URL: {}",
        serverInformation.getNetworkURL()
    );

    return Web3j.build(new HttpService(serverInformation.getNetworkURL(), httpClient));
  }

  /**
   * Creates a StaticGasProvider with gas price and limit.
   *
   * @param web3j Web3j instance.
   * @return StaticGasProvider instance.
   * @throws IOException If there is an error fetching gas price.
   */
  private StaticGasProvider createGasProvider(Web3j web3j) throws IOException {
    logger.debug("Fetching gas price from the Ethereum network.");

    BigInteger price = web3j.ethGasPrice()
        .send()
        .getGasPrice();
    BigInteger limit = BigInteger.valueOf(10000000L);

    logger.debug(
        "Gas price: {}, Gas limit: {}",
        price,
        limit
    );
    return new StaticGasProvider(
        price,
        limit
    );
  }

  /**
   * Loads the OpenDID contract.
   *
   * @param web3j       Web3j instance.
   * @param gasProvider Gas provider for transactions.
   * @param isReadOnly  Whether the contract is loaded in read-only mode.
   * @return OpenDID contract instance.
   * @throws IOException If there is an error loading the contract.
   */
  private OpenDID loadContract(Web3j web3j, StaticGasProvider gasProvider, boolean isReadOnly) {
    logger.debug(
        "Loading OpenDID contract. Read-only: {}",
        isReadOnly
    );

    if (isReadOnly) {
      return OpenDID.load(
          contractData.getContractAddress(),
          web3j,
          new ReadonlyTransactionManager(
              web3j,
              contractData.getContractAddress()
          ),
          gasProvider
      );
    } else {
      Credentials credentials = Credentials.create(contractData.getPrivateKey());
      return OpenDID.load(
          contractData.getContractAddress(),
          web3j,
          new RawTransactionManager(
              web3j,
              credentials,
              5,
              1000
          ),
          gasProvider
      );
    }
  }

  /**
   * Executes a contract function with error handling.
   *
   * @param contractFunction Function to execute.
   * @param isReadOnly       Whether the function is read-only.
   * @param <T>              Return type of the function.
   * @return Result of the function execution.
   * @throws BlockChainException If there is an error during execution.
   */
  private <T> T executeContract(Function<OpenDID, T> contractFunction, boolean isReadOnly)
      throws BlockChainException {
    logger.debug(
        "Executing contract function. Read-only: {}",
        isReadOnly
    );

    OkHttpClient httpClient = new OkHttpClient();
    try (Web3j web3j = createWeb3j(httpClient)) {
      StaticGasProvider gasProvider = createGasProvider(web3j);
      OpenDID contract = loadContract(
          web3j,
          gasProvider,
          isReadOnly
      );

      return contractFunction.apply(contract);
    } catch (ContractCallException e) {
      logger.error(
          "Error executing contract function: {}",
          e.getMessage(),
          e
      );

      throw new BlockChainException(
          BlockchainErrorCode.TRANSACTION_ERROR,
          e
      );

    } catch (Exception e) {
      logger.error(
          "Error executing contract function: {}",
          e.getMessage(),
          e
      );
      throw new BlockChainException(
          BlockchainErrorCode.CONNECTION_ERROR,
          e
      );
    } finally {
      httpClient.connectionPool().evictAll();
    }
  }

  /**
   * Registers a role for a specific address.
   *
   * @param address  The address to register the role for.
   * @param roleType The type of role to register.
   * @throws BlockChainException If there is an error during role registration.
   */
  public void registRole(String address, RoleType roleType) throws BlockChainException {

    logger.info(
        "Registering role: {} for address: {}",
        roleType,
        address
    );

    executeContract(
        contract -> {
          try {
            var receipt = contract.registRole(
                    address,
                    roleType.getRawValue()
                )
                .send();
            logger.debug("Transaction receipt: " + receipt.getTransactionHash());
            logger.debug("Transaction receipt status: " + receipt.getStatus());
            return null;
          } catch (Exception e) {
            logger.error(
                "Error registering role: " + e.getMessage(),
                e
            );
            throw new RuntimeException(e);
          }
        },
        false
    );
  }

  /**
   * Registers a DID Document on the blockchain and assigns a role to the registrant.
   *
   * @param invokedDidDoc The DID Document to be registered.
   * @param roleType      The role type to assign to the registrant.
   * @throws BlockChainException If there is an error during registration.
   */
  @Override
  public void registDidDoc(InvokedDidDoc invokedDidDoc, RoleType roleType)
      throws BlockChainException {

    executeContract(
        contract -> {
          try {
            String decodedDidDoc = new String(MultiBaseUtils.decode(invokedDidDoc.getDidDoc()));

            DidDocument didDocument = new DidDocument();
            didDocument.fromJson(decodedDidDoc);
            var document = EvmDataConverter.convertToContractObject(didDocument);
            logger.debug("Converted DID Document: " + document.id);
            contract.registDidDoc(document)
                .send();
            logger.info("DID Document registered successfully.");

            return null;
          } catch (Exception e) {
            logger.error(
                "Error registering DID Document: " + e.getMessage(),
                e
            );

            throw new RuntimeException(e);
          }
        },
        false
    );
  }

  /**
   * Retrieves a DID Document from the blockchain.
   *
   * @param didKeyUrl The DID key URL to retrieve the document for.
   * @return The retrieved DID Document object.
   * @throws BlockChainException If there is an error during retrieval.
   */
  @Override
  public Object getDidDoc(String didKeyUrl) throws BlockChainException {
    logger.info(
        "Retrieving DID Document for key URL: {}",
        didKeyUrl
    );

    return executeContract(
        contract -> {
          try {
            DidKeyUrlParser parser = new DidKeyUrlParser(didKeyUrl);
            var result = contract.getDidDoc(parser.getDid())
                .send();
            logger.info("Retrieved DID Document successfully.");

            return EvmDataConverter.convertToJavaObject(result);
          } catch (Exception e) {
            logger.error(
                "Error retrieving DID Document: " + e.getMessage(),
                e
            );

            throw new RuntimeException(e);
          }
        },
        true
    );
  }

  /**
   * Updates the status of a DID Document on the blockchain.
   *
   * @param didKeyUrl    The DID key URL of the document to update.
   * @param didDocStatus The new status to set for the document.
   * @return The status of the transaction or null.
   * @throws BlockChainException If there is an error during status update.
   */
  @Override
  public Object updateDidDocStatus(String didKeyUrl, DidDocStatus didDocStatus)
      throws BlockChainException {

    if (didDocStatus == DidDocStatus.TERMINATED) {
      throw new IllegalArgumentException("TERMINATED status requires a terminated time");
    }

    executeContract(
        contract -> {
          try {
            DidKeyUrlParser parser = new DidKeyUrlParser(didKeyUrl);
            var versionId = didDocStatus == DidDocStatus.REVOKED ? "" : parser.getVersionId();

            return contract.updateDidDocStatusInService(
                    parser.getDid(),
                    didDocStatus.getRawValue(),
                    versionId
                )
                .send()
                .getStatus();
          } catch (Exception e) {
            logger.error(
                "Error update document status: {}",
                e.getMessage(),
                e
            );

            throw new RuntimeException(e);
          }
        },
        false
    );
    return null;
  }

  /**
   * Updates the status of a DID Document with a termination time.
   *
   * @param didKeyUrl       The DID key URL of the document to update.
   * @param didDocStatus    The new status to set for the document.
   * @param terminatedTime  The termination time for the document.
   * @return An EvmResponse containing the transaction details.
   * @throws BlockChainException If there is an error during status update.
   */
  @Override
  public Object updateDidDocStatus(String didKeyUrl, DidDocStatus didDocStatus,
      LocalDateTime terminatedTime
  ) throws BlockChainException {

    return executeContract(
        contract -> {
          try {
            DidKeyUrlParser parser = new DidKeyUrlParser(didKeyUrl);

            var result = contract.updateDidDocStatusRevocation(
                    parser.getDid(),
                    didDocStatus.getRawValue(),
                    terminatedTime.format(DateTimeFormatter.ISO_DATE_TIME)
                )
                .send();

            logger.info("Update document status is successful.");

            return new EvmResponse(
                200,
                result.getStatus(),
                result.getTransactionHash()
            );
          } catch (Exception e) {
            logger.error(
                "Error update document status with termination time: {}",
                e.getMessage(),
                e
            );
            throw new RuntimeException(e);
          }
        },
        false
    );
  }

  /**
   * Registers Verifiable Credential metadata on the blockchain.
   *
   * @param vcMeta The VC metadata to register.
   * @throws BlockChainException If there is an error during registration.
   */
  @Override
  public void registVcMetadata(VcMeta vcMeta) throws BlockChainException {

    executeContract(
        contract -> {
          try {
            var vcMetaData = EvmDataConverter.convertToContractObject(vcMeta);

            contract.registVcMetaData(vcMetaData)
                .send();

            return null;
          } catch (Exception e) {
            logger.error(
                "Error registering VC metadata: {}",
                e.getMessage(),
                e
            );
            throw new RuntimeException(e);
          }
        },
        false
    );
  }

  /**
   * Retrieves Verifiable Credential metadata from the blockchain.
   *
   * @param vcId The ID of the VC to retrieve metadata for.
   * @return The retrieved VC metadata object.
   * @throws BlockChainException If there is an error during retrieval.
   */
  @Override
  public Object getVcMetadata(String vcId) throws BlockChainException {
    return executeContract(
        contract -> {
          try {
            var vcMeta = contract.getVcmetaData(vcId)
                .send();

            return EvmDataConverter.convertToJavaObject(vcMeta);
          } catch (Exception e) {
            logger.error(
                "Error retrieving VC metadata: {}",
                e.getMessage(),
                e
            );
            throw new RuntimeException(e);
          }
        },
        true
    );
  }

  /**
   * Updates the status of a Verifiable Credential.
   *
   * @param vcId     The ID of the Verifiable Credential to update.
   * @param vcStatus The new status to set for the credential.
   * @throws BlockChainException If there is an error during the status update.
   */
  @Override
  public void updateVcStatus(String vcId, VcStatus vcStatus) throws BlockChainException {
    executeContract(
        contract -> {
          try {
            contract.updateVcStats(
                vcId,
                vcStatus.getRawValue()
            ).send();

            return null;
          } catch (Exception e) {
            logger.error(
                "Error updating VC status: {}",
                e.getMessage(),
                e
            );
            throw new RuntimeException(e);
          }
        },
        false
    );
  }

  /**
   * Registers a Verifiable Credential schema on the blockchain.
   *
   * @param vcSchema The VC schema to register.
   * @throws BlockChainException If there is an error during registration.
   */
  @Override
  public void registVcSchema(VcSchema vcSchema) throws BlockChainException {
    executeContract(
        contract -> {
          try {
            var schema = EvmDataConverter.convertToContractObject(vcSchema);

            var receipt = contract.registVcSchema(schema)
                .send();

            logger.debug("Transaction receipt: " + receipt.getTransactionHash());
            logger.debug("Transaction receipt status: " + receipt.getStatus());

          } catch (Exception e) {
            logger.error("Error while registering VC schema: " + e.getMessage());
            throw new RuntimeException(e);
          }
          return null;
        },
        false
    );
  }

  /**
   * Retrieves a Verifiable Credential schema from the blockchain.
   *
   * @param schemaId The ID of the schema to retrieve.
   * @return The retrieved VC schema object.
   * @throws BlockChainException If there is an error during retrieval.
   */
  @Override
  public Object getVcSchema(String schemaId) throws BlockChainException {
    return executeContract(
        contract -> {
          try {
            var result = contract.getVcSchema(schemaId)
                .send();

            return EvmDataConverter.convertToJavaObject(result);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        },
        true
    );
  }

  /**
   * Registers a ZKP (Zero-Knowledge Proof) credential schema on the blockchain.
   *
   * @param credentialSchema The ZKP credential schema to register.
   * @throws BlockChainException If there is an error during registration.
   */
  @Override
  public void registZKPCredential(CredentialSchema credentialSchema) throws BlockChainException {

    executeContract(
        contract -> {
          try {
            var zkpCredentialSchema = EvmDataConverter.convertToContractObject(credentialSchema);
            var transactionReceipt = contract.registZKPCredential(zkpCredentialSchema)
                .send();

            logger.info("Transaction receipt: " + transactionReceipt.getTransactionHash());
            return null;
          } catch (Exception e) {
            logger.error(
                "Error registering ZKP credential: {}",
                e.getMessage(),
                e
            );
            throw new RuntimeException(e);
          }
        },
        false
    );
  }

  /**
   * Retrieves a ZKP (Zero-Knowledge Proof) credential schema from the blockchain.
   *
   * @param schemaId The ID of the credential schema to retrieve.
   * @return The retrieved ZKP credential schema object.
   * @throws BlockChainException If there is an error during retrieval.
   */
  @Override
  public Object getZKPCredential(String schemaId) throws BlockChainException {
    return executeContract(
        contract -> {
          try {
            OpenDID.ZKPLibrary_CredentialSchema credentialSchema = contract.getZKPCredential(schemaId)
                .send();

            return EvmDataConverter.convertToJavaObject(credentialSchema);
          } catch (Exception e) {
            logger.error(
                "Error retrieving ZKP credential: {}",
                e.getMessage(),
                e
            );
            throw new RuntimeException(e);
          }
        },
        true
    );
  }

  /**
   * Registers a ZKP (Zero-Knowledge Proof) credential definition on the blockchain.
   *
   * @param zkpCredentialDefinition The ZKP credential definition to register.
   * @throws BlockChainException If there is an error during registration.
   */
  @Override
  public void registZKPCredentialDefinition(
      org.omnione.did.zkp.datamodel.definition.CredentialDefinition zkpCredentialDefinition
  ) throws BlockChainException {
    logger.info("Registering ZKP Credential Definition: " + zkpCredentialDefinition.getId());

    executeContract(
        contract -> {
          try {
            CredentialDefinition credentialDefinition =
                EvmDataConverter.convertToContractObject(zkpCredentialDefinition);

            var transactionReceipt = contract.registZKPCredentialDefinition(credentialDefinition)
                .send();

            logger.debug("Transaction receipt hash : " + transactionReceipt.getTransactionHash());
            logger.debug("Transaction receipt status: " + transactionReceipt.getStatus());

            return null;
          } catch (Exception e) {
            logger.error("Error while registering ZKP credential definition: " + e.getMessage());

            throw new RuntimeException(e);
          }
        },
        false
    );
  }

  /**
   * Retrieves a ZKP (Zero-Knowledge Proof) credential definition from the blockchain.
   *
   * @param definitionId The ID of the credential definition to retrieve.
   * @return The retrieved ZKP credential definition object.
   * @throws BlockChainException If there is an error during retrieval.
   */
  @Override
  public Object getZKPCredentialDefinition(String definitionId) throws BlockChainException {

    return executeContract(
        contract -> {
          try {
            OpenDID.CredentialDefinition result = contract.getZKPCredentialDefinition(definitionId)
                .send();

            return EvmDataConverter.convertToJavaObject(result);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        },
        true
    );
  }
}
