package com.exclamationlabs.connid.base.connector.test.util;

import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ConnectorTestUtilsTest {

    protected TestConnector connector;

    @Before
    public void setup() {
        connector = new TestConnector();
    }

    @Test
    public void test() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);
        connector.executeQuery(ObjectClass.ACCOUNT, "query", resultsHandler,
                new OperationOptionsBuilder().build());
        assertTrue(idValues.contains("someId"));
        assertTrue(nameValues.contains("someName"));

    }

    static class TestConnector implements Connector, SearchOp<String> {

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
            handler.handle(
                    new ConnectorObjectBuilder()
                            .setUid("someId")
                            .setName("someName")
                            .setObjectClass(ObjectClass.ACCOUNT)
                            .build());
        }
    }
}
