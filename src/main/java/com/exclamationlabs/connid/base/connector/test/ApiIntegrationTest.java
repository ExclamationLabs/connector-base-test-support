/*
    Copyright 2022 Exclamation Labs

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

package com.exclamationlabs.connid.base.connector.test;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.api.APIConfiguration;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.api.ConnectorFacadeFactory;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.SearchResult;
import org.identityconnectors.framework.impl.api.APIConfigurationImpl;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.SearchResultsHandler;
import org.identityconnectors.test.common.TestHelpers;

import java.util.ArrayList;
import java.util.List;


/**
 * Abstract class for ConnId API facade-based tests that require
 * integration to an external data provider for authorization or Identity Access Management.
 */
public abstract class ApiIntegrationTest<T extends Configuration, U extends Connector>
        implements IntegrationTestHarness {

    Log LOG = Log.getLog(ApiIntegrationTest.class);

    /**
     * Return the configuration object applicable to this connector test
     * @return Concrete Configuration class that implements org.identityconnectors.framework.spi.Configuration.
     */
    protected abstract T getConfiguration();

    /**
     * Return the class of the connector under test.
     * @return Concrete Class that implements org.identityconnectors.framework.spi.Connector.
     */
    protected abstract Class<U> getConnectorClass();

    /**
     * Subclasses should invoke:
     * ConfigurationReader.setupTestConfiguration(configurationObject);
     *
     * This needs to implemented by the subclass because this test framework
     * does not have the base connector ConfigurationReader implementation code.
     *
     * @param configurationObject configuration object applicable to this connector test
     */
    protected abstract void readConfiguration(T configurationObject);

    protected ConnectorFacade connectorFacade;

    protected static List<ConnectorObject> results;

    protected SearchResultsHandler handler = new SearchResultsHandler() {

        @Override
        public boolean handle(ConnectorObject connectorObject) {
            results.add(connectorObject);
            LOG.ok("{0} added connectorObject {1} to results.  Results size now {2}",
                    this.getClass().getSimpleName(),
                    connectorObject,
                    results.size());
            return true;
        }

        @Override
        public void handleResult(SearchResult result) {
            LOG.ok("{0} handling {1}", this.getClass().getSimpleName(),
                    result.getRemainingPagedResults());
        }
    };

    public ConnectorFacade getConnectorFacade() {
        return connectorFacade;
    }

    public static List<ConnectorObject> getResults() {
        return results;
    }

    public SearchResultsHandler getHandler() {
        return handler;
    }

    /**
     * Default behavior is to disable connection pooling for API integration testing.
     * If for some reason connection pooling is desired, this method can be overloaded and
     * return true.
     * @return whether or not connection pooling should be used by ConnId facade for integration.
     */
    protected boolean usePooling() {
        return false;
    }

    protected void setup() {
        connectorFacade = ConnectorFacadeFactory.getInstance().newInstance(
                apiConfig(getConfiguration()));
        results = new ArrayList<>();
        LOG.ok("Setup facade and new results list for {0}", this.getClass().getSimpleName());

        validateConfiguration(getConfiguration());
    }

    protected APIConfiguration apiConfig(T configurationObject) {
        readConfiguration(configurationObject);
        APIConfiguration configuration = TestHelpers.createTestConfiguration(getConnectorClass(), configurationObject);
        ((APIConfigurationImpl) configuration).setConnectorPoolingSupported(usePooling());
        configuration.getResultsHandlerConfiguration().setFilteredResultsHandlerInValidationMode(true);
        return configuration;
    }

}
