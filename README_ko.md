# Server Blockchain SDK
Server Blockchain SDK Repository에 오신 것을 환영합니다.
이 Repository는 Blockchain 네트워크와 상호작용 하며, Samrt Contract 호출을 수행할 수 있는 SDK를 제공합니다.

## 폴더 구조
```
did-blockchain-sdk-server
├── CHANGELOG.md
├── CLA.md
├── CODE_OF_CONDUCT.md
├── LICENSE.dependencies.md
├── LICENSE.txt
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
    └── did-blockchain-sdk-server
        ├── README.md
        ├── README_ko.md
        ├── build.gradle
        ├── gradle
        ├── gradlew
        ├── gradlew.bat
        ├── setting.gradle
        └── src
```

|  이름                      |              역할                          |
| -------------------------- | ------------------------------------------ |
| CHANGELOG.md               | 프로젝트 버전별 변경사항                   |
| CLA.md                     | Contributor License Agreement              |
| CODE_OF_CONDUCT.md         | 기여자의 행동강령                          |
| CONTRIBUTING.md            | 기여 절차 및 방법                          |
| LICENSE.dependencies.md    | 프로젝트 의존성 라이브러리에 대한 라이선스 |
| LICENSE.txt                | 프로젝트 대한 라이선스                     |
| MAINTAINERS.md             | 유지관리 가이드                            |
| README.md                  | 프로젝트의 전체적인 개요 설명              |
| RELEASE-PROCESS.md         | 릴리즈 절차                                |
| SECURITY.md                | 보안취약점 보고 및 보안정책                | 
| docs                       | 문서                                       |
| ┖ api                      | API 가이드 문서                            |
| source                     | SDK 소스코드 프로젝트                      | 

<br>

## 라이브러리
라이브러리는 `release` 폴더에서 찾을 수 있습니다.
1. 프로젝트의 `libs`에 `did-datamodel-server-1.0.0.jar` 파일을 복사합니다.
2. 프로젝트의 `build.gradle`에 아래 의존성을 추가합니다.
```groovy
    implementation files('libs/did-datamodel-server-1.0.0.jar')
    implementation('org.hyperledger.fabric:fabric-gateway-java:2.2.9')
    implementation('com.fasterxml.jackson.core:jackson-databind:2.15.2')
    implementation('org.apache.commons:commons-pool2:2.12.0')
    testImplementation platform('org.junit:junit-bom:5.10.0')
    testImplementation('org.junit.jupiter:junit-jupiter')
    annotationProcessor('com.fasterxml.jackson.core:jackson-databind:2.15.2')
    annotationProcessor('org.projectlombok:lombok:1.18.28')
    compileOnly('org.projectlombok:lombok:1.18.28')
```
3. `Gradle`을 동기화하여 의존성이 제대로 추가되었는지 확인합니다.

## API 참조

API 참조는 [여기](source/did-blockchain-sdk-server/README.md)에서 확인할 수 있습니다.

## 기여
Contributing 및 pull request 제출 절차에 대한 자세한 내용은 [CONTRIBUTING.md](CONTRIBUTING.md)와 [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) 를 참조하세요.

## 라이선스
Copyright 2024 Raonsecure

