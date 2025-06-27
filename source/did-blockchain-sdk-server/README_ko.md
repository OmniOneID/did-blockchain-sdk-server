# Server Blockchain SDK Guide
본 문서는 OpenDID Server Blockchain SDK 사용을 위한 가이드로, 
Open DID에 필요한 DID Document(DID 문서), Verifiable Credential Meta(이하 VC Meta) 정보를 블록체인에 기록하기 위해 체인코드를 호출하고, 해당 트랜잭션 요청을 생성하는 기능을 제공합니다.


## S/W 사양
| 구분 | 내용         |
|------|------------|
| Language      | Java 21    |
| Build System  | Gradle 8.8 |

<br>

## 빌드 방법
: 본 SDK 그래들 프로젝트이므로 그래들이 설치 되어 있어야 한다.
1. 터미널을 열고 프로젝트 루트 디렉터리에서 `./gradlew clean build`를 실행합니다.
2. 빌드가 완료되면 `build/libs` 디렉터리에 `did-blockchain-sdk-server-2.0.0.jar` 파일이 생성됩니다.

<br>

## SDK 적용 방법
1. 프로젝트의 `libs`에 `did-datamodel-sdk-server-2.0.0.jar`, `did-zkp-sdk-server-2.0.0`, `did-crypto-sdk-server-2.0.0.jar` 파일들을 복사합니다.
2. 프로젝트의 `build.gradle`에 아래 의존성을 추가합니다.
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
3. `Gradle`을 동기화하여 의존성이 제대로 추가되었는지 확인합니다.

<br>

## API 규격서
| 구분 | API 문서 Link                                                                  |
|------|------------------------------------------------------------------------------|
| FabricContractApi  | [Blockchain SDK - FabricContracApi API](../../docs/api/Blockchain_API_ko.md) |
| ErrorCode          | [Error Code](../../docs/api/BlockchainErrorCode.md)                          |

### FabricContractApi
FabricContractApi는 Blockchain Network 설정 정보를 기반으로 DID 문서 및 VC Meta와 관련된 트랜잭션을 생성하여 체인코드와 상호작용하는 기능을 제공합니다.<br>주요 기능은 다음과 같습니다:

* <b>DID 문서 등록</b>: 새로운 DID 문서를 등록하고 상태를 저장합니다.
* <b>DID 문서 변경</b>: DID 문서를 변경합니다.
* <b>DID 문서 조회</b>: 특정 DID 문서와 해당 DID 문서 상태를 조회합니다.
* <b>DID 문서 상태 변경</b>: DID 문서 상태를 변경합니다.
* <b>VC Meta 등록</b>: VC 메타데이터를 등록합니다.
* <b>VC Meta 상태 변경</b>: VC 메타데이터의 상태를 변경합니다.
* <b>VC Meta 조회</b>: 특정 VC 메타데이터를 조회합니다.