package org.omnione.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.omnione.exception.BlockChainException;

public class DidKeyUrlParserTest {


  @Test
  void DidKeyUrlParsingTest_Success() {
    String didKeyUrl = "did:example:test#public-key-0";

    DidKeyUrlParser parser = assertDoesNotThrow(() -> new DidKeyUrlParser(didKeyUrl));

    assertEquals(parser.getDid(), "did:example:test");
    assertEquals(parser.getVersionId(), "");
  }

  @Test
  void DIDKeyUrlParsingTest_Fail() {
    List<String> invalidDidKeyUrls = new ArrayList<>();

    invalidDidKeyUrls.add("invalid:example:test?versionId=2#public-key-0");
    invalidDidKeyUrls.add("did:example?versionId=2#public-key-0");

    for (String invalid : invalidDidKeyUrls) {
      assertThrows(BlockChainException.class, () -> new DidKeyUrlParser(invalid));
    }
  }
}
