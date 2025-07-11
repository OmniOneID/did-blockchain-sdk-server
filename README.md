# Server Blockchain SDK

Welcome to the Server Blockchain SDK Repository.
This repository provides an SDK for interacting with Blockchain networks and invoking Smart Contracts.

## Folder Structure

```plaintext
did-blockchain-sdk-server
├── CHANGELOG.md
├── CLA.md
├── CODE_OF_CONDUCT.md
├── LICENSE
├── dependencies-license.md
├── CONTRIBUTING.md
├── MAINTAINERS.md
├── README.md
├── README_ko.md
├── RELEASE-PROCESS.md
├── SECURITY.md
├── docs
│   └── api
│       ├── Blockchain_API.md 
│       ├── Blockchain_API_ko.md 
│       └── BlockchainErrorCode.md 
└── source
    ├── did-blockchain-sdk-server
    │   ├── README.md
    │   ├── README_ko.md
    │   ├── build.gradle
    │   ├── gradle
    │   ├── libs
    │   ├── gradlew
    │   ├── gradlew.bat
    │   ├── setting.gradle
    │   └── src
    └── release
        └── did-blockchain-sdk-server-2.0.0.jar
```

| Name                    | Description                                     |
| ----------------------- | ----------------------------------------------- |
| CHANGELOG.md            | Version-specific changes in the project         |
| CLA.md                  | Contributor License Agreement                   |
| CODE_OF_CONDUCT.md      | Code of conduct for contributors                |
| CONTRIBUTING.md         | Contribution guidelines and procedures          |
| LICENSE                 | Apache 2.0                                      |
| dependencies-license.md | Licenses for the project’s dependency libraries |
| MAINTAINERS.md          | General guidelines for maintaining              |
| README.md               | Overview and description of the project         |
| RELEASE-PROCESS.md      | Release process                                 |
| SECURITY.md             | Security policy and vulnerability reporting     |
| docs                    | Documentation                                   |
| ┖ api                   | API guide documentation                         |
| source                  | SDK source code project                         |

<br>

## Libraries

Libraries can be found in the [Releases](https://github.com/OmniOneID/did-blockchain-sdk-server/releases).

## Blockchain SDK

1. Copy the `did-datamodel-sdk-server-2.0.0.jar`, `did-zkp-sdk-server-2.0.0`, `did-crypto-sdk-server-2.0.0.jar` files into the project's `libs` directory.

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

1. Synchronize `Gradle` to ensure that the dependencies are correctly added.

## API Reference

You can find the API reference [here](docs/api/Blockchain_API.md).

## Change Log

The Change Log provides a detailed record of version-specific changes and updates. You can find it here:

- [Change Log](./CHANGELOG.md)

## OpenDID Demonstration Videos <br>

To watch our demonstration videos of the OpenDID system in action, please visit our [Demo Repository](https://github.com/OmniOneID/did-demo-server). <br>
These videos showcase key features including user registration, VC issuance, and VP submission processes.

## Contributing

For detailed information on contributing and submitting pull requests, please refer to [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

## License

[Apache 2.0](LICENSE)
