package org.omnione.did.ethereum;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.util.Strings;
import org.omnione.did.ContractApi;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.schema.ClaimDef;
import org.omnione.did.data.model.schema.Namespace;
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.exception.BlockChainException;
import org.omnione.exception.BlockchainErrorCode;
import org.omnione.generated.OpenDID;
import org.omnione.generated.OpenDID.ClaimNamespace;
import org.omnione.generated.OpenDID.CredentialDefinition;
import org.omnione.generated.OpenDID.CredentialSubject;
import org.omnione.generated.OpenDID.SchemaClaimItem;
import org.omnione.generated.OpenDID.ZKPLibrary_CredentialSchema;
import org.omnione.response.EvmResponse;
import org.omnione.sender.ethereum.EvmContractData;
import org.omnione.sender.ethereum.EvmServerInformation;
import org.omnione.util.DidKeyUrlParser;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.exceptions.ContractCallException;
import org.web3j.tx.gas.StaticGasProvider;

public class EvmContractApi implements ContractApi {

  private final Log logger = LogFactory.getLog(EvmContractApi.class);
  private final EvmServerInformation serverInformation;
  private EvmContractData contractData;

  /**
   * Constructor to initialize server and contract information.
   *
   * @param resourcePath Path to the resource file containing configuration.
   * @throws IOException If there is an error reading the resource file.
   */
  public EvmContractApi(String resourcePath) throws IOException {
    logger.info("Initializing EvmContractApi with resource path: " + resourcePath);

    this.serverInformation = new EvmServerInformation(resourcePath);
    this.contractData = new EvmContractData(resourcePath);
  }

  /**
   * Creates a Web3j instance for interacting with the Ethereum network.
   *
   * @return Web3j instance.
   */
  private Web3j createWeb3j() {
    logger.debug("Creating Web3j instance for network URL: " + serverInformation.getNetworkURL());

    return Web3j.build(new HttpService(serverInformation.getNetworkURL()));
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

    logger.debug("Gas price: " + price + ", Gas limit: " + limit);
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
  private OpenDID loadContract(Web3j web3j, StaticGasProvider gasProvider, boolean isReadOnly)
      throws IOException {
    logger.debug("Loading OpenDID contract. Read-only: " + isReadOnly);

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
      Credentials credentials = Credentials.create(contractData.getContractAddress());
      return OpenDID.load(
          contractData.getContractAddress(),
          web3j,
          credentials,
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
    logger.debug("Executing contract function. Read-only: " + isReadOnly);

    try (Web3j web3j = createWeb3j()) {
      StaticGasProvider gasProvider = createGasProvider(web3j);
      OpenDID contract = loadContract(
          web3j,
          gasProvider,
          isReadOnly
      );

      return contractFunction.apply(contract);
    } catch (ContractCallException e) {
      logger.error(
          "Error executing contract function: " + e.getMessage(),
          e
      );

      throw new BlockChainException(
          BlockchainErrorCode.TRANSACTION_ERROR,
          e
      );

    } catch (Exception e) {
      logger.error(
          "Error executing contract function: " + e.getMessage(),
          e
      );
      throw new BlockChainException(
          BlockchainErrorCode.CONNECTION_ERROR,
          e
      );
    }
  }

  @Override
  public void registDidDoc(InvokedDidDoc invokedDidDoc, RoleType roleType)
      throws BlockChainException {

    logger.info("Registering DID Document with role type: " + roleType);
    executeContract(
        contract -> {
          try {
            var document = convertJsonToDocument(invokedDidDoc.getDidDoc());
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
   * Converts a JSON string to an OpenDID.Document object.
   *
   * @param json JSON string representing the DID Document.
   * @return OpenDID.Document object.
   */
  private OpenDID.Document convertJsonToDocument(String json) {
    logger.debug("Converting JSON to OpenDID.Document.");
    DidDocument didDocument = new DidDocument();
    didDocument.fromJson(json);

    var verificationMethodList = didDocument.getVerificationMethod()
        .stream()
        .map(value -> new OpenDID.VerificationMethod(
            value.getId(),
            new BigInteger(value.getType()),
            value.getController(),
            value.getPublicKeyMultibase(),
            new BigInteger(String.valueOf(value.getAuthType()))
        ))
        .toList();
    var servicesList = didDocument.getService()
        .stream()
        .map(value -> new OpenDID.Service(
            value.getId(),
            value.getType(),
            value.getServiceEndpoint()
        ))
        .toList();

    logger.debug("Converted OpenDID.Document successfully.");
    return new OpenDID.Document(
        didDocument.getContext(),
        didDocument.getId(),
        didDocument.getController(),
        didDocument.getCreated(),
        didDocument.getUpdated(),
        didDocument.getVersionId(),
        didDocument.getDeactivated(),
        verificationMethodList,
        didDocument.getAssertionMethod(),
        didDocument.getAuthentication(),
        didDocument.getKeyAgreement(),
        didDocument.getCapabilityInvocation(),
        didDocument.getCapabilityDelegation(),
        servicesList
    );
  }

  @Override
  public Object getDidDoc(String didKeyUrl) throws BlockChainException {
    logger.info("Retrieving DID Document for key URL: " + didKeyUrl);

    return executeContract(
        contract -> {
          try {
            DidKeyUrlParser parser = new DidKeyUrlParser(didKeyUrl);
            var result = contract.getDidDoc(parser.getDid())
                .send();
            logger.info("Retrieved DID Document successfully.");

            return result;
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
            var versionId =
                didDocStatus == DidDocStatus.REVOKED ? Strings.EMPTY : parser.getVersionId();

            return contract.updateDidDocStatusInService(
                    parser.getDid(),
                    didDocStatus.getRawValue(),
                    versionId
                )
                .send()
                .getStatus();
          } catch (Exception e) {
            logger.error(
                "Error update document status: " + e.getMessage(),
                e
            );

            throw new RuntimeException(e);
          }
        },
        false
    );
    return null;
  }

  @Override
  public Object updateDidDocStatus(String didKeyUrl, DidDocStatus didDocStatus,
                                   LocalDateTime terminatedTime
  ) throws BlockChainException {

    DidKeyUrlParser parser = new DidKeyUrlParser(didKeyUrl);
    try (Web3j web3j = Web3j.build(new HttpService(serverInformation.getNetworkURL()))) {
      Credentials credentials = Credentials.create(contractData.getPrivateKey());
      BigInteger gasPrice = web3j.ethGasPrice()
          .send()
          .getGasPrice();
      BigInteger gasLimit = BigInteger.valueOf(10000000L);
      StaticGasProvider gasProvider = new StaticGasProvider(
          gasPrice,
          gasLimit
      );

      var contract = OpenDID.load(
          contractData.getContractAddress(),
          web3j,
          credentials,
          gasProvider
      );

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
    } catch (ContractCallException e) {
      throw new BlockChainException(
          BlockchainErrorCode.TRANSACTION_ERROR,
          new Error("Contract call error: " + e.getMessage())
      );
    } catch (Exception e) {
      throw new BlockChainException(
          BlockchainErrorCode.CONNECTION_ERROR,
          new Error("Network error: " + e.getMessage())
      );
    }
  }

  @Override
  public void registVcMetadata(VcMeta vcMeta) throws BlockChainException {

    var vcMetaData = convertVcMetaToVcMetaData(vcMeta);

    try (Web3j web3j = Web3j.build(new HttpService(serverInformation.getNetworkURL()))) {
      Credentials credentials = Credentials.create(contractData.getPrivateKey());
      BigInteger gasPrice = web3j.ethGasPrice()
          .send()
          .getGasPrice();
      BigInteger gasLimit = BigInteger.valueOf(10000000L);
      StaticGasProvider gasProvider = new StaticGasProvider(
          gasPrice,
          gasLimit
      );

      var contract = OpenDID.load(
          contractData.getContractAddress(),
          web3j,
          credentials,
          gasProvider
      );

      contract.registVcMetaData(vcMetaData)
          .send();
    } catch (ContractCallException e) {
      throw new BlockChainException(
          BlockchainErrorCode.TRANSACTION_ERROR,
          new Error("Contract call error: " + e.getMessage())
      );
    } catch (Exception e) {
      throw new BlockChainException(
          BlockchainErrorCode.CONNECTION_ERROR,
          new Error("Network error: " + e.getMessage())
      );
    }
  }

  private OpenDID.VcMeta convertVcMetaToVcMetaData(VcMeta vcMeta) {
    return new OpenDID.VcMeta(
        vcMeta.getId(),
        new OpenDID.Provider(
            vcMeta.getIssuer()
                .getDid(),
            vcMeta.getIssuer()
                .getCertVcRef()
        ),
        vcMeta.getSubject(),
        new OpenDID.CredentialSchemaLibrary_CredentialSchema(
            vcMeta.getCredentialSchema()
                .getId(),
            vcMeta.getCredentialSchema()
                .getType()
        ),
        vcMeta.getStatus(),
        vcMeta.getIssuanceDate(),
        vcMeta.getValidFrom(),
        vcMeta.getValidUntil(),
        vcMeta.getFormatVersion(),
        vcMeta.getLanguage()
    );
  }

  @Override
  public Object getVcMetadata(String vcId) throws BlockChainException {
    try (Web3j web3j = Web3j.build(new HttpService(serverInformation.getNetworkURL()))) {
      BigInteger gasPrice = web3j.ethGasPrice()
          .send()
          .getGasPrice();
      BigInteger gasLimit = BigInteger.valueOf(10000000L);
      ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(
          web3j,
          contractData.getContractAddress()
      );
      var contract = OpenDID.load(
          contractData.getContractAddress(),
          web3j,
          readonlyTransactionManager,
          new StaticGasProvider(
              gasPrice,
              gasLimit
          )
      );

      return contract.getVcmetaData(vcId)
          .send();
    } catch (ContractCallException e) {
      throw new BlockChainException(
          BlockchainErrorCode.TRANSACTION_ERROR,
          new Error("Contract call error: " + e.getMessage())
      );
    } catch (Exception e) {
      throw new BlockChainException(
          BlockchainErrorCode.CONNECTION_ERROR,
          new Error("Network error: " + e.getMessage())
      );
    }
  }

  @Override
  public void updateVcStatus(String vcId, VcStatus vcStatus) throws BlockChainException {
    try (Web3j web3j = Web3j.build(new HttpService(serverInformation.getNetworkURL()))) {
      Credentials credentials = Credentials.create(contractData.getPrivateKey());
      BigInteger gasPrice = web3j.ethGasPrice()
          .send()
          .getGasPrice();
      BigInteger gasLimit = BigInteger.valueOf(10000000L);
      StaticGasProvider gasProvider = new StaticGasProvider(
          gasPrice,
          gasLimit
      );

      var contract = OpenDID.load(
          contractData.getContractAddress(),
          web3j,
          credentials,
          gasProvider
      );

      contract.updateVcStats(
          vcId,
          vcStatus.getRawValue()
      );
    } catch (ContractCallException e) {
      throw new BlockChainException(
          BlockchainErrorCode.TRANSACTION_ERROR,
          new Error("Contract call error: " + e.getMessage())
      );
    } catch (Exception e) {
      throw new BlockChainException(
          BlockchainErrorCode.CONNECTION_ERROR,
          new Error("Network error: " + e.getMessage())
      );
    }
  }

  @Override
  public void registVcSchema(VcSchema vcSchema) throws BlockChainException {
    executeContract(
        contract -> {
          try {

            var contractMetaData = new OpenDID.MetaData(
                vcSchema.getMetadata()
                    .getFormatVersion(),
                vcSchema.getMetadata()
                    .getLanguage()
            );
            var claimsList = vcSchema.getCredentialSubject()
                .getClaims();

            List<CredentialSubject> contractCredentialSubjectList = new ArrayList<>();
            List<OpenDID.VCSchemaClaim> vcSchemaClaimList = new ArrayList<>();
            for (SchemaClaims claims : claimsList) {

              List<SchemaClaimItem> contractClaimItems = new ArrayList<>();
              for (ClaimDef claimDef : claims.getItems()) {
                var item = new OpenDID.SchemaClaimItem(
                    claimDef.getCaption(),
                    claimDef.getFormat(),
                    claimDef.isHideValue(),
                    claimDef.getId(),
                    claimDef.getType()
                );
                contractClaimItems.add(item);
              }

              Namespace namespace = claims.getNamespace();
              OpenDID.ClaimNamespace contractNameSpace = new ClaimNamespace(
                  namespace.getId(),
                  namespace.getName(),
                  namespace.getRef()
              );
              OpenDID.VCSchemaClaim vcSchemaClaim = new OpenDID.VCSchemaClaim(
                  contractClaimItems,
                  contractNameSpace
              );

              vcSchemaClaimList.add(vcSchemaClaim);
            }
            OpenDID.CredentialSubject credentialSubject =
                new OpenDID.CredentialSubject(vcSchemaClaimList);


            var contractVcSchema = new OpenDID.VcSchema(
                vcSchema.getId(),
                vcSchema.getSchema(),
                vcSchema.getTitle(),
                vcSchema.getDescription(),
                contractMetaData,
                credentialSubject
            );
            var receipt = contract.registVcSchema(contractVcSchema)
                .send();

            logger.debug("Transaction receipt: " + receipt.getTransactionHash());
            logger.debug("Transaction receipt status: " + receipt.getStatus());

          } catch (Exception e) {
            logger.error("Error while registering VC schema: " + e.getMessage());
          }
          return null;
        },
        false
    );
  }

  @Override
  public Object getVcSchema(String schemaId) throws BlockChainException {
    return executeContract(
        contract -> {
          try {
            return contract.getVcSchema(schemaId)
                .send();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        },
        true
    );
  }

  @Override
  public void registZKPCredential(CredentialSchema credentialSchema) throws BlockChainException {

    try (Web3j web3j = Web3j.build(new HttpService(serverInformation.getNetworkURL()))) {
      Credentials credentials = Credentials.create(contractData.getPrivateKey());
      BigInteger gasPrice = web3j.ethGasPrice()
          .send()
          .getGasPrice();
      BigInteger gasLimit = BigInteger.valueOf(10000000L);
      StaticGasProvider gasProvider = new StaticGasProvider(
          gasPrice,
          gasLimit
      );

      OpenDID contract = OpenDID.load(
          contractData.getContractAddress(),
          web3j,
          credentials,
          gasProvider
      );

      var zkpCredentialSchema = new ZKPLibrary_CredentialSchema(
          credentialSchema.getId(),
          credentialSchema.getName(),
          credentialSchema.getVersion(),
          credentialSchema.getAttrNames(),
          credentialSchema.getTag()
      );

      var transactionReceipt = contract.registZKPCredential(zkpCredentialSchema)
          .send();

      logger.info("Transaction receipt: " + transactionReceipt.getTransactionHash());
    } catch (ContractCallException e) {
      throw new BlockChainException(
          BlockchainErrorCode.TRANSACTION_ERROR,
          new Error("Contract call error: " + e.getMessage())
      );
    } catch (Exception e) {
      throw new BlockChainException(
          BlockchainErrorCode.CONNECTION_ERROR,
          new Error("Network error: " + e.getMessage())
      );
    }

  }

  @Override
  public Object getZKPCredential(String schemaId) throws BlockChainException {
    try (Web3j web3j = Web3j.build(new HttpService(serverInformation.getNetworkURL()))) {
      BigInteger gasPrice = web3j.ethGasPrice()
          .send()
          .getGasPrice();
      BigInteger gasLimit = BigInteger.valueOf(10000000L);
      ReadonlyTransactionManager readonlyTransactionManager = new ReadonlyTransactionManager(
          web3j,
          contractData.getContractAddress()
      );
      var contract = OpenDID.load(
          contractData.getContractAddress(),
          web3j,
          readonlyTransactionManager,
          new StaticGasProvider(
              gasPrice,
              gasLimit
          )
      );

      return contract.getZKPCredential(schemaId)
          .send();

    } catch (ContractCallException e) {
      throw new BlockChainException(
          BlockchainErrorCode.TRANSACTION_ERROR,
          new Error("Contract call error: " + e.getMessage())
      );
    } catch (Exception e) {
      throw new BlockChainException(
          BlockchainErrorCode.CONNECTION_ERROR,
          new Error("Network error: " + e.getMessage())
      );
    }
  }

  @Override
  public void registZKPCredentialDefinition(
      org.omnione.did.zkp.datamodel.definition.CredentialDefinition zkpCredentialDefinition
  ) throws BlockChainException {
    logger.info("Registering ZKP Credential Definition: " + zkpCredentialDefinition.getId());

    executeContract(
        contract -> {
          try {
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(zkpCredentialDefinition.getValue()
                .getPrimary());
            CredentialDefinition credentialDefinition = new CredentialDefinition(
                zkpCredentialDefinition.getId(),
                zkpCredentialDefinition.getSchemaId(),
                zkpCredentialDefinition.getVer(),
                Integer.toString(zkpCredentialDefinition.getType()
                    .getValue()),
                value,
                zkpCredentialDefinition.getTag()
            );

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

  @Override
  public Object getZKPCredentialDefinition(String definitionId) throws BlockChainException {

    return executeContract(
        contract -> {
          try {
            return contract.getZKPCredentialDefinition(definitionId)
                .send();

          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        },
        true
    );
  }
}
