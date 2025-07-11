# Server Blockchain SDK Guide

This document serves as a guide for using the OpenDID Server Blockchain SDK. It provides functionality to invoke contract and generate transaction requests necessary to record DID Document and Verifiable Credential Metadata (VC Meta) information on the blockchain for OpenDID.

## S/W Specifications

| Category     | Details    |
| ------------ | ---------- |
| Language     | Java 21    |
| Build System | Gradle 8.8 |

<br>

## Build Method

: Since this SDK is a Gradle project, Gradle must be installed

1. Open a terminal and navigate to the project root directory, then run `./gradlew clean build`.
2. Once the build is complete, the `did-blockchain-sdk-server-2.0.0.jar` file will be generated in the `{projectPath}/build/libs` directory.

<br>

## SDK Application Method

1. Copy the `did-datamodel-sdk-server-2.0.0.jar`, `did-zkp-sdk-server-2.0.0`, `did-crypto-sdk-server-2.0.0.jar` files into the project's `{projectPath}/libs` directory.
2. Add the following dependencies to the project's `build.gradle` file:

```groovy
    implementation files("libs/did-datamodel-sdk-server-2.0.0.jar")
    implementation files("libs/did-zkp-sdk-server-2.0.0.jar")
    implementation files("libs/did-crypto-sdk-server-2.0.0.jar")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.hyperledger.fabric:fabric-gateway:${fabricGatewayVersion}")
    implementation platform('com.google.protobuf:protobuf-bom:4.29.2')
    implementation("org.web3j:core:${web3jCoreVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("org.apache.commons:commons-pool2:2.12.0")
    implementation 'org.hibernate.validator:hibernate-validator:8.0.0.Final'
```

3. Synchronize `Gradle` to ensure that the dependencies are correctly added.

## API Documentation

| Category          | API Documentation Link                                                      |
| ----------------- | --------------------------------------------------------------------------- |
| FabricContractApi | [Blockchain SDK - FabricContractApi API](../../docs/api/Blockchain_API.md)  |
| EvmContractApi    | [Blockchain SDK - EvmContractApi API](../../docs/api/Blockchain_EVM_API.md) |
| ErrorCode         | [Error Code](../../docs/api/BlockchainErrorCode.md)                         |

### ContractApi

The ContractApi provides functionality to interact with chaincode by creating transactions related to DID documents and VC Meta, based on the Blockchain Network configuration information.<br>Key features include:

* <b>Register DID Document</b>: Registers a new DID document and saves its state.
* <b>Update DID Document</b>: Modifies an existing DID document.
* <b>Retrieve DID Document</b>: Retrieves a specific DID document and its state.
* <b>Change DID Document State</b>: Changes the state of a DID document.
* <b>Register VC Meta</b>: Registers VC metadata.
* <b>Change VC Meta State</b>: Updates the state of VC metadata.
* <b>Retrieve VC Meta</b>: Retrieves specific VC metadata.
