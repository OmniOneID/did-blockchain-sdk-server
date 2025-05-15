package org.omnione.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.omnione.did.data.model.util.json.GsonWrapper;

@Getter
@Setter
@NoArgsConstructor
public class EvmResponse extends OmnioneResponse {


  @Expose
  @SerializedName("status")
  private int status;

  @Expose
  @SerializedName("message")
  private String message;

  @Expose
  @SerializedName("payload")
  private String payload;

  public EvmResponse(int status, String message, String payload) {
    this.status = status;
    this.message = message;
    this.payload = payload;
  }

  /**
   * Populates the fields of this {@code EvmResponse} instance by deserializing the provided JSON
   * string.
   *
   * <p>This method uses a custom Gson wrapper to convert the JSON string into a
   * {@code EvmResponse} object, and then copies the data into the current instance.</p>
   *
   * @param val the JSON string representing a {@code EvmResponse} object
   */
  public void fromJson(String val) {
    GsonWrapper gson = new GsonWrapper();
    EvmResponse data = gson.fromJson(
        val,
        EvmResponse.class
    );

    status = data.status;
    message = data.message;
    payload = data.payload;
  }

}
