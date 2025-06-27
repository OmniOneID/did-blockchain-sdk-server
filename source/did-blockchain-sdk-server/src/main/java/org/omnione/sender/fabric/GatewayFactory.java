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
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Hash;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;

/**
 * The {@code GatewayFactory} class is responsible for creating and managing instances of the {@link
 * Gateway} class, which are used to interact with a Hyperledger Fabric network.
 *
 * <p>This class extends {@link BasePooledObjectFactory} and is designed to be used in conjunction
 * with a pooling mechanism to efficiently manage {@code Gateway} connections.
 */
@RequiredArgsConstructor
public class GatewayFactory extends BasePooledObjectFactory<Gateway> {

  private final X509Identity identity;
  private final String configFilePath;
  private final ManagedChannel channel;

  /**
   * Creates a new {@link Gateway} instance configured with the identity and network configuration.
   *
   * <p>The {@code Gateway} is configured with a commit timeout of 7 seconds and is set to force
   * close any existing connections.
   *
   * @return a newly created {@code Gateway} instance
   * @throws Exception if an error occurs during the creation of the {@code Gateway}
   */
  @Override
  public Gateway create() throws Exception {

    return Gateway.newInstance()
        .identity(identity)
        .signer(newSigner())
        .hash(Hash.SHA256)
        .connection(channel)
        .evaluateOptions(callOptions -> callOptions.withDeadlineAfter(7, TimeUnit.SECONDS))
        .endorseOptions(callOptions -> callOptions.withDeadlineAfter(7, TimeUnit.SECONDS))
        .submitOptions(callOptions -> callOptions.withDeadlineAfter(7, TimeUnit.SECONDS))
        .commitStatusOptions(callOptions -> callOptions.withDeadlineAfter(7, TimeUnit.SECONDS))
        .connect();
  }

  private Signer newSigner() throws IOException, InvalidKeyException {
    try (var keyReader = Files.newBufferedReader(Paths.get(configFilePath))) {
      var privateKey = Identities.readPrivateKey(keyReader);
      return Signers.newPrivateKeySigner(privateKey);
    }
  }

  /**
   * Wraps the provided {@link Gateway} instance in a {@link PooledObject} for use in a pool.
   *
   * @param gateway the {@code Gateway} instance to be wrapped
   * @return a {@code PooledObject} wrapping the {@code Gateway} instance
   */
  @Override
  public PooledObject<Gateway> wrap(Gateway gateway) {
    return new DefaultPooledObject<>(gateway);
  }

  /**
   * Destroys the provided {@link Gateway} instance, closing any underlying connections.
   *
   * @param p the {@code PooledObject} wrapping the {@code Gateway} instance to be destroyed
   * @param destroyMode the mode in which the object is being destroyed
   */
  @Override
  public void destroyObject(PooledObject<Gateway> p, DestroyMode destroyMode) {
    p.getObject().close();
  }
}
