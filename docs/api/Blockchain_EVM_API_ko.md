---
puppeteer:
    pdf:
        format: A4
        displayHeaderFooter: true
        landscape: false
        scale: 0.8
        margin:
            top: 1.2cm
            right: 1cm
            bottom: 1cm
            left: 1cm
    image:
        quality: 100
        fullPage: false
---

# 블록체인 SDK API

- **주제:** 블록체인 SDK API
- **작성자:** 김민용
- **날짜:** 2025-05-07
- **버전:** v1.0.0

| 버전   | 날짜       | 변경사항        |
| ------ | ---------- | --------------- |
| v1.0.0 | 2025-05-07 | 초기 버전       |

---

## 목차

1. [DID 문서 등록](#1-did-문서-등록)  
2. [DID 문서 조회](#2-did-문서-조회)  
3. [DID 문서 상태 변경](#3-did-문서-상태-변경)  
    - [3.1. ACTIVATED, DEACTIVATED, REVOKED](#31-activated-deactivated-revoked)  
    - [3.2. TERMINATED](#32-terminated)  
4. [VC 메타데이터 등록](#4-vc-메타데이터-등록)  
5. [VC 메타데이터 조회](#5-vc-메타데이터-조회)  
6. [VC 상태 변경](#6-vc-상태-변경)  

---

## 설정

```properties
# EVM 네트워크 구성
evm.network.url=http://localhost:8083
evm.chainId=1337
evm.gas.limit=100000000
evm.gas.price=0
evm.connection.timeout=10000

# EVM 컨트랙트 구성
evm.contract.address=0xa0E49611FB410c00f425E83A4240e1681c51DDf4
evm.contract.privateKey=0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63
```

---

## 1. DID 문서 등록

- **클래스 명:** `EvmContractApi`  
- **함수 명:** `registDidDoc`  
- **함수 설명:** DID 문서의 최초 등록 또는 업데이트 (상태 변경 제외).

### 입력 매개변수

| 매개변수        | 타입            | 설명                                            | 필수/선택 | 비고                    |
| --------------- | --------------- | ----------------------------------------------- | --------- | ----------------------- |
| invokedDocument | InvokedDocument | 등록 및 업데이트용 문서                         | 필수      |                         |
| roleType        | RoleType        | 제공자 타입 열거형                              | 필수      | 데이터 명세서 참조      |

**출력:** void

#### 함수 선언

```java
void registDidDoc(InvokedDocument invokedDocument, RoleType roleType) throws BlockchainException;
```

#### 함수 사용법

```java
void registDidDocTest() throws Exception {
    String document = "z29KbMzaxCukd5iyCozP3CQEHiR...CkRAC8usDbDrsvxPc5";
    Provider controller = Provider.builder()...build();
    Proof proof = Proof.builder()...build();

    InvokedDocument invokedDocument = InvokedDocument.builder()
                                        .didDoc(document)
                                        .controller(controller)
                                        .proof(proof)
                                        .nonce("12345")
                                        .build();

    EvmContractApi contractApi = (EvmContractApi) ContractFactory.EVM.create("junit-platform.properties");
    contractApi.registDidDoc(invokedDocument, RoleType.TAS);
}
```

---

## 2. DID 문서 조회

- **클래스 명:** `EvmContractApi`  
- **함수 명:** `getDidDoc`  
- **함수 설명:** 특정 DID 문서를 조회합니다.

### 입력 매개변수

| 매개변수  | 타입   | 설명               | 필수/선택 |
| --------- | ------ | ------------------ | --------- |
| didKeyUrl | string | DID 키 식별자 URL  | 필수      |

### 출력 매개변수

| 타입            | 설명                   | 필수/선택 | 비고                                                                                |
| --------------- | ---------------------- | --------- | ----------------------------------------------------------------------------------- |
| DidDocAndStatus | DidDocAndStatus 객체   | 필수      | 현재는 EVMResponse 객체; 추후 전용 DidDocAndStatus로 개선 예정                      |

#### 함수 선언

```java
DidDocAndStatus getDidDoc(String didKeyUrl) throws BlockChainException;
```

#### 함수 사용법

```java
void getDidDocTest() throws Exception {
    String didKeyUrl = "did:odi:das/segment?versionId=1#public-key-0";
    EvmContractApi contractApi = (EvmContractApi) ContractFactory.EVM.create("junit-platform.properties");
    DidDocAndStatus didDocAndStatus = contractApi.getDidDoc(didKeyUrl);
}
```

---

## 3. DID 문서 상태 변경

- **클래스 명:** `EvmContractApi`  
- **함수 명:** `updateDidDocStatus`  
- **함수 설명:** DID 문서의 상태를 변경합니다.  
  상태 = TERMINATED인 경우, `terminatedTime`이 필요합니다.

| 상태        | 명칭   | 설명                                                                          |
| ----------- | ------ | ----------------------------------------------------------------------------- |
| ACTIVATED   | 활성   | DEACTIVATED 또는 REVOKED로 전환 가능                                          |
| DEACTIVATED | 비활성 | ACTIVATED 또는 REVOKED로 전환 가능                                            |
| REVOKED     | 폐기   | TERMINATED로 전환 가능                                                        |
| TERMINATED  | 종료   | 더 이상 전환 불가; 종료 시작 날짜 필요                                        |

### 3.1. ACTIVATED, DEACTIVATED, REVOKED

#### 입력 매개변수

| 매개변수       | 타입          | 설명               | 필수/선택 |
| -------------- | ------------- | ------------------ | --------- |
| didKeyUrl      | string        | DID 키 식별자 URL  | 필수      |
| didDocStatus   | DidDocStatus  | 변경할 상태        | 필수      |

### 출력 매개변수

| 타입            | 설명                   | 필수/선택 | 비고                                        |
| --------------- | ---------------------- | --------- | ------------------------------------------- |
| DidDocAndStatus | DidDocAndStatus 객체   | 필수      | 현재는 EVMResponse 객체; 추후 개선 예정     |

#### 함수 선언

```java
DidDocAndStatus updateDidDocStatus(String didKeyUrl, DidDocStatus didDocStatus) throws BlockChainException;
```

#### 함수 사용법

```java
void updateDidDocStatusTest() throws Exception {
    String didKeyUrl = "did:odi:das/segment?versionId=1#public-key-0";
    EvmContractApi contractApi = (EvmContractApi) ContractFactory.EVM.create("junit-platform.properties");
    DidDocAndStatus didDocAndStatus = contractApi.updateDidDocStatus(didKeyUrl, DidDocStatus.REVOKED);
}
```

### 3.2. TERMINATED

#### 입력 매개변수

| 매개변수        | 타입          | 설명               | 필수/선택 |
| --------------- | ------------- | ------------------ | --------- |
| didKeyUrl       | string        | DID 키 식별자 URL  | 필수      |
| didDocStatus    | DidDocStatus  | 변경할 상태        | 필수      |
| terminatedTime  | LocalDateTime | 종료 시간          | 필수      |

### 출력 매개변수

| 타입            | 설명                   | 필수/선택 | 비고                                        |
| --------------- | ---------------------- | --------- | ------------------------------------------- |
| DidDocAndStatus | DidDocAndStatus 객체   | 필수      | 현재는 EVMResponse 객체; 추후 개선 예정     |

#### 함수 선언

```java
DidDocAndStatus updateDidDocStatus(String didKeyUrl, DidDocStatus didDocStatus, LocalDateTime terminatedTime) throws BlockChainException;
```

#### 함수 사용법

```java
void updateDidDocStatusTerminatedTest() throws Exception {
    String didKeyUrl = "did:odi:das/segment?versionId=1#public-key-0";
    LocalDateTime terminatedTime = LocalDateTime.now();
    EvmContractApi contractApi = (EvmContractApi) ContractFactory.EVM.create("junit-platform.properties");
    DidDocAndStatus didDocAndStatus = contractApi.updateDidDocStatus(didKeyUrl, DidDocStatus.TERMINATED, terminatedTime);
}
```

---

## 4. VC 메타데이터 등록

- **클래스 명:** `EvmContractApi`  
- **함수 명:** `registVcMetadata`  
- **함수 설명:** VC 메타데이터를 등록합니다.

### 입력 매개변수

| 매개변수 | 타입   | 설명          | 필수/선택 |
| -------- | ------ | ------------- | --------- |
| vcMeta   | VcMeta | VC 메타데이터 | 필수      |

**출력:** void

#### 함수 선언

```java
void registVcMetadata(VcMeta vcMeta) throws BlockChainException;
```

#### 함수 사용법

```java
void registVcMetadataTest() throws Exception {
    VcMeta vcMeta = VcMeta.builder()
                        .id("vc-id-123")
                        .issuer("did:odi:issuer")
                        .subject("did:odi:subject")
                        .validFrom(LocalDateTime.now())
                        .validUntil(LocalDateTime.now().plusYears(1))
                        .build();

    EvmContractApi contractApi = (EvmContractApi) ContractFactory.EVM.create("junit-platform.properties");
    contractApi.registVcMetadata(vcMeta);
}
```

---

## 5. VC 메타데이터 조회

- **클래스 명:** `EvmContractApi`  
- **함수 명:** `getVcMetadata`  
- **함수 설명:** 특정 VC의 메타데이터를 조회합니다.

### 입력 매개변수

| 매개변수 | 타입   | 설명    | 필수/선택 |
| -------- | ------ | ------- | --------- |
| vcId     | string | VC ID   | 필수      |

### 출력 매개변수

| 타입   | 설명          | 필수/선택 | 비고                                        |
| ------ | ------------- | --------- | ------------------------------------------- |
| VcMeta | VC 메타데이터 | 필수      | 현재는 EVMResponse 객체; 추후 개선 예정     |

#### 함수 선언

```java
VcMeta getVcMetadata(String vcId) throws BlockChainException;
```

#### 함수 사용법

```java
void getVcMetadataTest() throws Exception {
    String vcId = "vc-id-123";
    EvmContractApi contractApi = (EvmContractApi) ContractFactory.EVM.create("junit-platform.properties");
    VcMeta vcMeta = contractApi.getVcMetadata(vcId);
}
```

---

## 6. VC 상태 변경

- **클래스 명:** `EvmContractApi`  
- **함수 명:** `updateVcStatus`  
- **함수 설명:** VC의 상태를 변경합니다.

### 입력 매개변수

| 매개변수 | 타입     | 설명       | 필수/선택 |
| -------- | -------- | ---------- | --------- |
| vcId     | string   | VC ID      | 필수      |
| vcStatus | VcStatus | 변경할 상태| 필수      |

**출력:** void

#### 함수 선언

```java
void updateVcStatus(String vcId, VcStatus vcStatus) throws BlockChainException;
```

#### 함수 사용법

```java
void updateVcStatusTest() throws Exception {
    String vcId = "vc-id-123";
    EvmContractApi contractApi = (EvmContractApi) ContractFactory.EVM.create("junit-platform.properties");
    contractApi.updateVcStatus(vcId, VcStatus.REVOKED);
}
```