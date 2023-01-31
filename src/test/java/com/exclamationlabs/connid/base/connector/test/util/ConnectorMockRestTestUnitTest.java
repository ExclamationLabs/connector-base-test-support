package com.exclamationlabs.connid.base.connector.test.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opentest4j.AssertionFailedError;

@ExtendWith(MockitoExtension.class)
public class ConnectorMockRestTestUnitTest extends ConnectorMockRestTest {

  protected TestConnector connector;
  protected ResultsHandler resultsHandler;

  @BeforeEach
  public void setup() {
    connector = new TestConnector();
    resultsHandler = ConnectorTestUtils.buildResultsHandler(new ArrayList<>(), new ArrayList<>());
  }

  @Test
  public void test() {
    prepareMockResponse("{someResponse:0}");
    connector.executeQuery(
        ObjectClass.ACCOUNT, "query", resultsHandler, new OperationOptionsBuilder().build());
  }

  @Test
  public void testWithHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("bert", "ernie");
    headers.put("elmo", "grover");
    prepareMockResponse(headers, "{someResponse:0}");
    connector.executeQuery(
        ObjectClass.ACCOUNT, "query", resultsHandler, new OperationOptionsBuilder().build());
  }

  @Test
  public void testInvalidNullMock() {
    assertThrows(AssertionFailedError.class, () -> prepareMockResponse((String[]) null));
  }

  @Test
  public void testInvalidEmptyStringListMock() {
    assertThrows(AssertionFailedError.class, () -> prepareMockResponse(new String[0]));
  }

  @Test
  public void testInvalidEmptyStringListMockFirstNull() {
    prepareMockResponse(new String[] {null});
    connector.executeQuery(
        ObjectClass.ACCOUNT, "query", resultsHandler, new OperationOptionsBuilder().build());
  }

  @Test
  public void testInvalidEmptyStringListMockMixedNull() {
    prepareMockResponse((String) null, "hi", null);
    connector.executeQuery(
        ObjectClass.ACCOUNT, "query", resultsHandler, new OperationOptionsBuilder().build());
  }

  @Test
  public void testMultiple() {
    prepareMockResponse("{someResponse:0}", "{anotherResponse:1}", "{yetAnotherResponse:2}");
    connector.executeQuery(
        ObjectClass.ACCOUNT, "query", resultsHandler, new OperationOptionsBuilder().build());
  }

  @Test
  public void testEmptyResponse() {
    prepareMockResponse("");
    connector.executeQuery(
        ObjectClass.ACCOUNT, "query", resultsHandler, new OperationOptionsBuilder().build());
  }

  @Test
  public void testEmpty() {
    prepareMockResponse();
    connector.executeQuery(
        ObjectClass.ACCOUNT, "query", resultsHandler, new OperationOptionsBuilder().build());
  }

  @Test
  public void testException() {
    prepareClientException(new ConnectorException("failed"));
    assertThrows(
        ConnectorException.class,
        () ->
            connector.executeQuery(
                ObjectClass.ACCOUNT,
                "query",
                resultsHandler,
                new OperationOptionsBuilder().build()));
  }

  @Test
  public void testErrorResponseBody() {
    prepareClientFaultResponse("{oops:99}", HttpStatus.SC_BAD_REQUEST);
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            connector.executeQuery(
                ObjectClass.ACCOUNT,
                "query",
                resultsHandler,
                new OperationOptionsBuilder().build()));
  }

  @Test
  public void testErrorResponseBodyDifferentCode() {
    prepareClientFaultResponse("{oops:888}", HttpStatus.SC_FORBIDDEN);
    assertThrows(
        ConnectorSecurityException.class,
        () ->
            connector.executeQuery(
                ObjectClass.ACCOUNT,
                "query",
                resultsHandler,
                new OperationOptionsBuilder().build()));
  }

  class TestConnector implements Connector, SearchOp<String> {

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
      try {
        HttpResponse response = stubClient.execute(new HttpGet());
        HttpEntity entity = response.getEntity();
        StatusLine statusLine = response.getStatusLine();
        String responseBody = "dummy";
        if (entity != null) {
          entity.getContent();
        }
        if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
          if (statusLine.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
            throw new InvalidAttributeValueException(responseBody);
          } else {
            throw new ConnectorSecurityException(responseBody);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
