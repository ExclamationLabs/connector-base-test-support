package com.exclamationlabs.connid.base.connector.test.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class ConnectorMockRestTestUnitTest extends ConnectorMockRestTest {

    protected TestConnector connector;

    @Before
    public void setup() {
        connector = new TestConnector();
    }

    @Test
    public void test() {
        prepareMockResponse("{someResponse:0}");
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(new ArrayList<>(),
                new ArrayList<>());
        connector.executeQuery(ObjectClass.ACCOUNT, "query", resultsHandler,
                new OperationOptionsBuilder().build());
    }

    @Test
    public void testEmptyResponse() {
        prepareMockResponse("");
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(new ArrayList<>(),
                new ArrayList<>());
        connector.executeQuery(ObjectClass.ACCOUNT, "query", resultsHandler,
                new OperationOptionsBuilder().build());
    }

    @Test
    public void testEmpty() {
        prepareMockResponseEmpty();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(new ArrayList<>(),
                new ArrayList<>());
        connector.executeQuery(ObjectClass.ACCOUNT, "query", resultsHandler,
                new OperationOptionsBuilder().build());
    }

    class TestConnector implements Connector, SearchOp<String> {

        @Override
        public Configuration getConfiguration() {
            return null;
        }

        @Override
        public void init(Configuration cfg) {

        }

        @Override
        public void dispose() {

        }

        @Override
        public FilterTranslator<String> createFilterTranslator(ObjectClass objectClass, OperationOptions options) {
            return null;
        }

        @Override
        public void executeQuery(ObjectClass objectClass, String query, ResultsHandler handler, OperationOptions options) {
            try {
                HttpResponse response = stubClient.execute(new HttpGet());
                HttpEntity entity = response.getEntity();
                StatusLine statusLine = response.getStatusLine();
                if (entity != null) {
                    entity.getContent();
                }
                statusLine.getStatusCode();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
