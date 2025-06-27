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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.stream.Stream;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import lombok.Getter;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.omnione.exception.BlockChainException;
import org.omnione.exception.BlockchainErrorCode;
import org.omnione.sender.ServerInformation;

/**
 * The {@code FabricServerInformation} class extends {@link ServerInformation} and manages the
 * configuration and gateway pool for connecting to a Hyperledger Fabric network.
 *
 * <p>This class is responsible for loading server configuration from a properties file,
 * initializing the identity and gateway pool, and managing the lifecycle of gateway connections.
 */
@Getter
public class FabricServerInformation extends ServerInformation {

  private final String mspId;
  private final String configFilePath;
  private final String privateKeyFilePath;
  private final String certificateFilePath;
  private final String networkName;
  private final String chaincodeName;
  private final String tlsFilePath;
  private final String serverEndpoint;
  private GenericObjectPool<Gateway> gatewayPool;
  private X509Identity identity;

  /**
   * Constructs a {@code FabricServerInformation} object by loading configuration properties from
   * the specified resource file and initializing the identity and gateway pool.
   *
   * @param resource the name of the properties file to load the configuration from
   * @throws CertificateException if there is an error reading the X.509 certificate
   * @throws InvalidKeyException if there is an error reading the private key
   * @throws IOException if there is an error loading the properties file
   */
  public FabricServerInformation(String resource)
      throws CertificateException, InvalidKeyException, IOException {
    super();

    Properties properties = loadProperties(resource);

    this.mspId = properties.getProperty("fabric.mspId");
    this.configFilePath = properties.getProperty("fabric.configFilePath");
    this.privateKeyFilePath = properties.getProperty("fabric.privateKeyFilePath");
    this.certificateFilePath = properties.getProperty("fabric.certificateFilePath");
    this.networkName = properties.getProperty("fabric.networkName");
    this.chaincodeName = properties.getProperty("fabric.chaincodeName");
    this.tlsFilePath = properties.getProperty("fabric.tlsFilePath");
    this.serverEndpoint = properties.getProperty("fabric.serverEndpoint");

    initializeIdentity();
    initializeGatewayPool();
  }

  /**
   * Reads the content of a file from the specified path and returns it as a {@code String}.
   *
   * @param path the file path to read from
   * @return the content of the file as a {@code String}
   */
  private String getFileContent(String path) throws IOException {

    StringBuilder stringBuilder = new StringBuilder();

    Stream<String> stream = Files.lines(Paths.get(path));
    stream.forEach(s -> stringBuilder.append(s).append('\n'));

    return stringBuilder.toString();
  }

  /**
   * Initializes the X.509 identity using the certificate and private key files specified in the
   * properties.
   *
   * @throws CertificateException if there is an error reading the X.509 certificate
   * @throws InvalidKeyException if there is an error reading the private key
   */
  private void initializeIdentity() throws CertificateException, InvalidKeyException, IOException {
    String certificateFile = getFileContent(certificateFilePath);
    X509Certificate certificate = Identities.readX509Certificate(certificateFile);

    String privateFile = getFileContent(privateKeyFilePath);
    PrivateKey privateKey = Identities.readPrivateKey(privateFile);

    this.identity = new X509Identity(mspId, certificate);
  }

  /** Initializes the gateway connection pool for interacting with the Fabric network. */
  private void initializeGatewayPool() throws IOException {
    ManagedChannel channle = newGrpcChannel();
    GatewayFactory gatewayFactory = new GatewayFactory(identity, configFilePath, channle);
    GenericObjectPoolConfig<Gateway> poolConfig = new GenericObjectPoolConfig<>();
    poolConfig.setMaxTotal(10);
    poolConfig.setMinIdle(2);
    poolConfig.setMaxIdle(5);

    this.gatewayPool = new GenericObjectPool<>(gatewayFactory, poolConfig);
  }

  private ManagedChannel newGrpcChannel() throws IOException {
    ChannelCredentials credentials =
        TlsChannelCredentials.newBuilder().trustManager(Paths.get(tlsFilePath).toFile()).build();

    return Grpc.newChannelBuilder(this.serverEndpoint, credentials)
        .overrideAuthority("peer0.org1.example.com")
        .build();
  }

  /**
   * Retrieves a gateway connection from the pool for interacting with the Fabric network.
   *
   * @return a {@code Gateway} connection
   * @throws BlockChainException if there is an error obtaining a gateway connection
   */
  public Gateway getGateway() throws BlockChainException {
    try {
      return gatewayPool.borrowObject();
    } catch (Exception e) {
      throw new BlockChainException(BlockchainErrorCode.CONNECTION_ERROR, e);
    }
  }

  /**
   * Returns a gateway connection to the pool after use.
   *
   * @param gateway the {@code Gateway} connection to return
   */
  public void returnGateway(Gateway gateway) {
    gatewayPool.returnObject(gateway);
  }

  /** Closes the gateway connection pool and releases all resources. */
  public void closePool() {
    gatewayPool.close();
  }
}
