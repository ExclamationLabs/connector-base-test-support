package com.exclamationlabs.connid.base.connector.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConnectorTestUtilsTest {

  protected TestConnector connector;

  @BeforeEach
  public void setup() {
    connector = new TestConnector();
  }

  @Test
  public void test() {
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);
    connector.executeQuery(
        ObjectClass.ACCOUNT, "query", resultsHandler, new OperationOptionsBuilder().build());
    assertTrue(idValues.contains("someId"));
    assertTrue(nameValues.contains("someName"));
  }
  @Test
  public void testResults() {
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    List<ConnectorObject> results = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues, results);
    connector.executeQuery(
            ObjectClass.ACCOUNT, "query", resultsHandler, new OperationOptionsBuilder().build());
    assertTrue(idValues.contains("someId"));
    assertTrue(nameValues.contains("someName"));
    assertEquals("someName", results.get(0).getAttributeByName("__NAME__").getValue().get(0));
    assertEquals("someName", results.get(0).getAttributeByName("__NAME__").getValue().get(0));
  }

  static class TestConnector implements Connector, SearchOp<String> {

    @Override
    public Configuration getConfiguration() {
      return null;
    }

    @Override
    public void init(Configuration cfg) {}

    @Override
    public void dispose() {}

    @Override
    public FilterTranslator<String> createFilterTranslator(
        ObjectClass objectClass, OperationOptions options) {
      return null;
    }

    @Override
    public void executeQuery(
        ObjectClass objectClass, String query, ResultsHandler handler, OperationOptions options) {
      handler.handle(
          new ConnectorObjectBuilder()
              .setUid("someId")
              .setName("someName")
              .setObjectClass(ObjectClass.ACCOUNT)
              .build());
    }
  }
}
