---
marp: false
theme: default
paginate: false
style: |
  section {
    @import url('https://cdn.jsdelivr.net/gh/sunn-us/SUITE/fonts/static/woff2/SUITE.css');

    font-family: 'SUITE', sans-serif;
    font-size: 20px;
    padding: 40px;
    padding-right: 200px;
    border: 4px solid orange;
    box-sizing: border-box;
    border-radius: 16px;
  }

  h1, h2, h3, h4 {
    color: #FF8C00;
    border-bottom: 2px solid #e6e6e6;
    padding-bottom: 5px;
  }

  section::before {
    content: "";
    position: absolute;
    top: 24px;
    right: 24px;
    width: 200px;
    height: 200px;
    background-image: url('https://omnioneid.github.io/assets/images/logo.png');
    background-size: contain;
    background-repeat: no-repeat;
    background-position: top right;
    z-index: 10;
  }

  a {
    color: #FF9E1B;
    text-decoration: none;
  }

---

# Server Blockchain SDK Guide

본 문서는 OpenDID Server Blockchain SDK 사용을 위한 가이드로, 
Open DID에 필요한 DID Document(DID 문서), Verifiable Credential Meta(이하 VC Meta) 정보를 블록체인에 기록하기 위해 체인코드를 호출하고, 해당 트랜잭션 요청을 생성하는 기능을 제공합니다.

## S/W 사양

| 구분         | 내용       |
| ------------ | ---------- |
| Language     | Java 17    |
| Build System | Gradle 8.8 |

---

## 빌드 방법

: 본 SDK 그래들 프로젝트이므로 그래들이 설치 되어 있어야 한다.

1. 프로젝트의 `libs`에 `did-datamodel-server-1.0.1.jar` 파일을 복사합니다.
2. 프로젝트의 `build.gradle`에 아래 의존성을 추가합니다.
3. `Gradle`을 동기화하여 의존성이 제대로 추가되었는지 확인합니다.
4. 터미널을 열고 프로젝트 루트 디렉터리에서 `./gradlew clean build`를 실행합니다.
5. 빌드가 완료되면 `build/libs` 디렉터리에 `did-blockchain-sdk-server-1.0.0.jar` 파일이 생성됩니다.

### 의존성 정보

```groovy
    implementation files('libs/did-datamodel-sdk-server-1.0.1.jar')
    implementation('org.hyperledger.fabric:fabric-gateway-java:2.2.9')
    implementation("org.web3j:core:4.13.0")
    implementation('com.fasterxml.jackson.core:jackson-databind:2.15.2')
    implementation('org.apache.commons:commons-pool2:2.12.0')
    implementation ('org.hibernate.validator:hibernate-validator:8.0.0.Final')
    implementation ('jakarta.validation:jakarta.validation-api:3.1.1')
    annotationProcessor('com.fasterxml.jackson.core:jackson-databind:2.15.2')
    annotationProcessor('org.projectlombok:lombok:1.18.28')
    compileOnly('org.projectlombok:lombok:1.18.28')
```

---

## Properties 설정

```properties
# EVM Network Configuration
evm.network.url=http://localhost:8083      # The URL of the EVM network to connect to
evm.chainId=1337                           # The unique identifier for the blockchain network (Chain ID)
evm.gas.limit=100000000                    # The maximum amount of gas allowed for transactions
evm.gas.price=0                            # The price of gas per unit (set to 0 for testing purposes)
evm.connection.timeout=10000               # The timeout duration (in milliseconds) for network connections

# EVM Contract Configuration
evm.contract.address=0xa0E49611FB410c00f425E83A4240e1681c51DDf4  # The address of the deployed smart contract
evm.contract.privateKey=0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63  # The private key for signing transactions
```

## SDK 사용 에시

```java
void updateVcStatusTest() throws Exception {

    String vcId = "vcId";

    // Set blockchain network information and create API object
    EvmContractApi contractApi = (EvmContractApi) ContractFactory.EVM.create("junit-platform.properties");

    // Call
    contractApi.updateVcStatus(vcId, VcStatus.INACTIVE);
}
```

---

## DID Document Registration

DID 문서의 초기 등록 또는 상태를 제외한 DID 문서의 업데이트

### Input Parameters

| Parameter       | Type            | Description                                      | **M/O** | **Notes**              |
| --------------- | --------------- | ------------------------------------------------ | ------- | ---------------------- |
| invokedDocument | InvokedDocument | Document for DID registration and update request | M       |                        |
| roleType        | RoleType        | Enum for provider type                           | M       | See Data Specification |

### Output Parameters

`void`

---

## DID Document Retrieval

DID 기반 특정 Document 조회

### Input Parameters

| Parameter | Type   | Description            | **M/O** | **Notes** |
| --------- | ------ | ---------------------- | ------- | --------- |
| didKeyUrl | string | DID key identifier URL | M       |           |

### Output Parameters

| Type            | Description            | **M/O** | **Notes**                                                                                           |
| --------------- | ---------------------- | ------- | --------------------------------------------------------------------------------------------------- |
| DidDocAndStatus | DidDocAndStatus object | M       | As of v0.0.1, it is specified as a EVMResponse object <br/> DidDocAndStatus will be reflected later |

---

## 3. DID Document Status Change

특정한 DID 문서의 상태를 변경합니다.

| Status Value | Name       | Description                                                                                  |
| ------------ | ---------- | -------------------------------------------------------------------------------------------- |
| ACTIVATED    | Active     | &bull; Can transition to DEACTIVATED or REVOKED status                                       |
| DEACTIVATED  | Inactive   | &bull; Can transition to ACTIVATED or REVOKED status                                         |
| REVOKED      | Revoked    | &bull; Can transition to TERMINATED status                                                   |
| TERMINATED   | Terminated | &bull; Cannot transition to any other status </br> &bull; Termination start date is required |

---

### 3.1. ACTIVATED, DEACTIVATED, REVOKED

### Input Parameters

| Parameter    | Type         | Description              | **M/O** | **Remarks** |
| ------------ | ------------ | ------------------------ | ------- | ----------- |
| didKeyUrl    | string       | DID key identifier URL   | M       |             |
| didDocStatus | DidDocStatus | DID document status Enum | M       |             |

### Output Parameters

| Type        | Description   | **M/O** | **Notes** |
| ----------- | ------------- | ------- | --------- |
| DidDocument | DidDoc object | M       |           |

---

### 3.2. TERMINATED

### Input Parameters

| Parameter      | Type          | Description               | **M/O** | **Notes** |
| -------------- | ------------- | ------------------------- | ------- | --------- |
| didKeyUrl      | string        | DID key identifier URL    | M       |           |
| didDocStatus   | DidDocStatus  | DID document status Enum  | M       |           |
| terminatedTime | LocalDateTime | Start time of termination | M       |           |

### Output Parameters

| Type        | Description   | **M/O** | **Notes** |
| ----------- | ------------- | ------- | --------- |
| DidDocument | DidDoc object | M       |           |

---

## VC Metadata Registration

VC metadata 등록

### Input Parameters

| Parameter | Type   | Description | **M/O** | **Notes** |
| --------- | ------ | ----------- | ------- | --------- |
| vcMeta    | VcMeta | VC metadata | M       |           |

### Output Parameters
void

---

## VC metadata Retrieval

특정한 VC의 metadata 조회

### Input Parameters

| Parameter | Type   | Description | **M/O** | **Notes** |
| --------- | ------ | ----------- | ------- | --------- |
| vcId      | string | VC ID       | M       |           |


### Output Parameters

| Type   | Description   | **M/O** | **Notes** |
| ------ | ------------- | ------- | --------- |
| VcMeta | VcMeta Object | M       |           |

---

## 6. VC Status Change

특정한 VC의 상태 변경

### Input Parameters

| Parameter | Type     | Description    | **M/O** | **Notes** |
| --------- | -------- | -------------- | ------- | --------- |
| vcId      | string   | VC ID          | M       |           |
| vcStatus  | VcStatus | VC status Enum | M       |           |

> VC Status Enum
>
> - ACTIVE
> - INACTIVE
> - REVOKED

### Output Parameters

void
