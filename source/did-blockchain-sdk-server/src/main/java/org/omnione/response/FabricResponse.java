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

package org.omnione.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.omnione.did.data.model.util.json.GsonWrapper;

/**
 * The {@code FabricResponse} class represents a response from a Fabric network operation. It
 * encapsulates the status, message, and payload returned by the operation.
 */
@Getter
@Setter
@NoArgsConstructor
public class FabricResponse extends OmnioneResponse {

  @Expose
  @SerializedName("status")
  private int status;

  @Expose
  @SerializedName("message")
  private String message;

  @Expose
  @SerializedName("payload")
  private String payload;

  /**
   * Populates the fields of this {@code FabricResponse} instance by deserializing the provided JSON
   * string.
   *
   * <p>This method uses a custom Gson wrapper to convert the JSON string into a
   * {@code FabricResponse} object, and then copies the data into the current instance.</p>
   *
   * @param val the JSON string representing a {@code FabricResponse} object
   */
  public void fromJson(String val) {
    GsonWrapper gson = new GsonWrapper();
    FabricResponse data = gson.fromJson(
        val,
        FabricResponse.class
    );
    status = data.status;
    message = data.message;
    payload = data.payload;
  }
}
