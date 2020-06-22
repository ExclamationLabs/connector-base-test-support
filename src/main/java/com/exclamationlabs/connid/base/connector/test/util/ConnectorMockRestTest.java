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

import com.exclamationlabs.connid.base.connector.test.IntegrationTest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.identityconnectors.common.logging.Log;
import org.junit.Assert;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

/**
 * Subclass this abstract in order to add Mock REST support
 * for testing connectors that use RESTful drivers which
 * subclass BaseRestDriver.
 */
@SuppressWarnings("unused") // used by downstream projects
public abstract class ConnectorMockRestTest {

    private static final Log LOG = Log.getLog(IntegrationTest.class);

    @Mock
    protected HttpClient stubClient;

    @Mock
    protected HttpResponse stubResponse;

    @Mock
    protected HttpEntity stubResponseEntity;

    @Mock
    protected StatusLine stubStatusLine;

    @SuppressWarnings("unused") // used by downstream projects
    protected void prepareMockResponse(String responseData) {
        try {
            Mockito.when(stubResponseEntity.getContent()).thenReturn(new ByteArrayInputStream(responseData.getBytes()));
            Mockito.when(stubResponse.getEntity()).thenReturn(stubResponseEntity);
            Mockito.when(stubResponse.getStatusLine()).thenReturn(stubStatusLine);
            Mockito.when(stubStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            Mockito.when(stubClient.execute(any(HttpRequestBase.class))).thenReturn(stubResponse);
        } catch(IOException ioe) {
            handleFailure("IO Exception occurred during Mock rest execution " +
                    "(populated response)", ioe);
        }
    }

    @SuppressWarnings("unused") // used by downstream projects
    protected void prepareMockResponseEmpty() {
        try {
            Mockito.when(stubResponse.getStatusLine()).thenReturn(stubStatusLine);
            Mockito.when(stubStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            Mockito.when(stubClient.execute(any(HttpRequestBase.class))).thenReturn(stubResponse);
        } catch(IOException ioe) {
            handleFailure("IO Exception occurred during Mock rest execution " +
                    "(empty response", ioe);
        }
    }

    private static void handleFailure(String message, IOException ioe) {
        LOG.error(message, ioe);
        Assert.fail(message);
    }
}
