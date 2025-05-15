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

package org.omnione.sender;

import java.io.IOException;
import java.util.Properties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Abstract base class for storing and managing server information.
 */
@Getter
@Setter
@RequiredArgsConstructor
public abstract class ServerInformation {

  private String host;
  private int port;

  /**
   * Loads properties from the specified resource.
   * <p>
   * This method supports both absolute file paths and classpath resources. If the given resource is
   * an absolute path, it loads the properties file directly from the file system. Otherwise, it
   * attempts to load the properties from the classpath.
   *
   * @param resource the absolute file path or classpath resource name of the properties file
   * @return a {@code Properties} object containing the configuration
   * @throws IOException if the properties file is not found or cannot be loaded
   */
  protected Properties loadProperties(String resource) throws IOException {
    return SenderUtils.loadProperties(resource);
  }
}
