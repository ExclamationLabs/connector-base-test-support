/*
    Copyright 2020 Exclamation Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.exclamationlabs.connid.base.connector.test.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.identityconnectors.common.logging.Log;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Subclass this abstract in order to add Mock REST support for testing connectors that use RESTful
 * drivers which subclass BaseRestDriver.
 */
public abstract class ConnectorMockRestTest {

  private static final Log LOG = Log.getLog(ConnectorMockRestTest.class);

  @Mock protected HttpClient stubClient;

  @Mock protected HttpResponse stubResponse;

  @Mock protected HttpEntity stubResponseEntity;

  @Mock protected StatusLine stubStatusLine;

  protected Header[] stubHeaders;

  protected void prepareClientException(Throwable exception) {
    try {
      Mockito.when(stubClient.execute(any(HttpRequestBase.class))).thenThrow(exception);
    } catch (IOException ioe) {
      handleFailure(
          "IO Exception occurred during Mock rest execution " + "(populated client exception)",
          ioe);
    }
  }

  protected void prepareClientFaultResponse(String jsonErrorResponseData, int httpStatus) {
    try {
      Mockito.when(stubResponseEntity.getContent())
          .thenReturn(new ByteArrayInputStream(jsonErrorResponseData.getBytes()));
      Mockito.when(stubResponse.getEntity()).thenReturn(stubResponseEntity);
      Mockito.when(stubResponse.getStatusLine()).thenReturn(stubStatusLine);
      Mockito.when(stubStatusLine.getStatusCode()).thenReturn(httpStatus);
      Mockito.when(stubClient.execute(any(HttpRequestBase.class))).thenReturn(stubResponse);
    } catch (IOException ioe) {
      handleFailure(
          "IO Exception occurred during Mock rest execution " + "(populated fault response)", ioe);
    }
  }

  /** Deprecated - use prepareMockResponse() (no parameter method) */
  @Deprecated
  protected void prepareMockResponseEmpty() {
    prepareMockResponse();
  }

  protected void prepareMockResponse() {
    try {
      Mockito.when(stubResponse.getStatusLine()).thenReturn(stubStatusLine);
      Mockito.when(stubStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
      Mockito.when(stubClient.execute(any(HttpRequestBase.class))).thenReturn(stubResponse);
    } catch (IOException ioe) {
      handleFailure("IOException occurred during Mock rest execution for empty response", ioe);
    }
  }

  protected void prepareMockResponse(Map<String, String> responseHeaders, String... responseData) {
    if (checkResponseData(responseData)) {
      return;
    }

    stubHeaders = new Header[responseHeaders.size()];
    int headerCtr = 0;
    for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
      stubHeaders[headerCtr++] =
          new Header() {
            @Override
            public HeaderElement[] getElements() throws ParseException {
              return new HeaderElement[0];
            }

            @Override
            public String getName() {
              return entry.getKey();
            }

            @Override
            public String getValue() {
              return entry.getValue();
            }
          };
    }

    Mockito.lenient().when(this.stubResponse.getAllHeaders()).thenReturn(stubHeaders);
    prepareMockResponse(responseData);
  }

  protected void prepareMockResponse(String... responseData) {
    try {
      if (checkResponseData(responseData)) {
        return;
      }

      if (responseData.length == 1) {
        Mockito.when(this.stubResponseEntity.getContent())
            .thenReturn(
                new ByteArrayInputStream(
                    responseData[0] == null ? "".getBytes() : responseData[0].getBytes()));
        Mockito.when(this.stubResponse.getEntity()).thenReturn(this.stubResponseEntity);
        Mockito.when(this.stubResponse.getStatusLine()).thenReturn(this.stubStatusLine);
        Mockito.when(this.stubStatusLine.getStatusCode()).thenReturn(200);
        Mockito.when(this.stubClient.execute(ArgumentMatchers.any(HttpRequestBase.class)))
            .thenReturn(this.stubResponse);
      } else {
        String[] dataAfterFirst = ArrayUtils.remove(responseData, 0);
        List<ByteArrayInputStream> streamsAfterFirst = new ArrayList<>();
        for (String current : dataAfterFirst) {
          streamsAfterFirst.add(
              new ByteArrayInputStream(current == null ? "".getBytes() : current.getBytes()));
        }
        ByteArrayInputStream[] streamsAfterFirstArray =
            new ByteArrayInputStream[dataAfterFirst.length];
        streamsAfterFirstArray = streamsAfterFirst.toArray(streamsAfterFirstArray);

        HttpEntity[] entities = new HttpEntity[dataAfterFirst.length];
        Arrays.fill(entities, this.stubResponseEntity);

        StatusLine[] statusLines = new StatusLine[dataAfterFirst.length];
        Arrays.fill(statusLines, this.stubStatusLine);

        Integer[] statusCodes = new Integer[dataAfterFirst.length];
        Arrays.fill(statusCodes, HttpStatus.SC_OK);

        HttpResponse[] httpResponses = new HttpResponse[dataAfterFirst.length];
        Arrays.fill(httpResponses, stubResponse);

        Mockito.when(this.stubResponseEntity.getContent())
            .thenReturn(
                new ByteArrayInputStream(
                    responseData[0] == null ? "".getBytes() : responseData[0].getBytes()),
                streamsAfterFirstArray);
        Mockito.when(this.stubResponse.getEntity()).thenReturn(this.stubResponseEntity, entities);
        Mockito.when(this.stubResponse.getStatusLine())
            .thenReturn(this.stubStatusLine, statusLines);
        Mockito.when(this.stubStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK, statusCodes);
        Mockito.when(this.stubClient.execute(ArgumentMatchers.any(HttpRequestBase.class)))
            .thenReturn(this.stubResponse, httpResponses);
      }

    } catch (IOException ioe) {
      handleFailure("IOException occurred during Mock rest execution for data response", ioe);
    }
  }

  protected void prepareMockResponse(Map<String, String> uriToResponseMap) {
    try {
      Mockito.when(this.stubClient.execute(ArgumentMatchers.any(HttpRequestBase.class)))
          .thenAnswer(invocation -> {
              HttpRequestBase request = invocation.getArgument(0);
              String uri = request.getURI().toString();
              String responseData = uriToResponseMap.getOrDefault(uri, "");
              
              // Set up response for this specific URI
              Mockito.when(this.stubResponseEntity.getContent())
                  .thenReturn(new ByteArrayInputStream(responseData.getBytes()));
              Mockito.when(this.stubResponse.getEntity()).thenReturn(this.stubResponseEntity);
              Mockito.when(this.stubResponse.getStatusLine()).thenReturn(this.stubStatusLine);
              Mockito.when(this.stubStatusLine.getStatusCode()).thenReturn(200);
              
              return this.stubResponse;
          });
    } catch (IOException ioe) {
      handleFailure("IOException occurred during Mock rest execution for URI-specific response", ioe);
    }
  }

  private boolean checkResponseData(String... responseData) {
    if (responseData == null || responseData.length == 0) {
      Throwable error = new IllegalAccessException("Invalid Null or empty mock response supplied");
      handleFailure(error.getMessage(), error);
      return true;
    }
    return false;
  }

  private static void handleFailure(String message, Throwable throwable) {
    LOG.error(message, throwable);
    fail(message);
  }
}
