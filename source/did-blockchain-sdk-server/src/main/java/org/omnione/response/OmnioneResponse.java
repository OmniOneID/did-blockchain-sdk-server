package org.omnione.response;

public abstract class OmnioneResponse {

  /**
   * Populates the fields of this {@code OmnioneResponse} instance by deserializing the provided
   * JSON string.
   *
   * <p>This method uses a custom Gson wrapper to convert the JSON string into a
   * {@code OmnioneResponse} object, and then copies the data into the current instance.</p>
   *
   * @param val the JSON string representing a {@code OmnioneResponse} object
   */
  public abstract void fromJson(String val);
}
