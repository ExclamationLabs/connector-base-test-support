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

package com.exclamationlabs.connid.base.connector.test;


import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.junit.Assume;

/**
 * Interface for tests that require integration to an external data
 * provider for authorization or Identity Access Management.
 */
public abstract class IntegrationTest {

    Log LOG = Log.getLog(IntegrationTest.class);

    public abstract String getConfigurationName();

    protected final void setup(Connector connector, Configuration configuration) {
        boolean success = false;
        try {
            connector.init(configuration);
            success = true;
        } catch (ConfigurationException ce) {
            LOG.info("Connector Integration test could not be run: " + ce.getMessage());
            if (isContinuousIntegrationBuild()) {
                throw new IllegalArgumentException(
                        "Configuration invalid or secret linkage missing for " +
                                "connector integration test running on CI server", ce);
            }
        }
        Assume.assumeTrue(success);
    }

    protected final void setup(Configuration configuration) {
        boolean success = false;
        try {
            configuration.validate();
            success = true;
        } catch (ConfigurationException ce) {
            LOG.info("Isolated Integration test could not be run: " + ce.getMessage());
            if (isContinuousIntegrationBuild()) {
                throw new IllegalArgumentException(
                        "Configuration invalid or secret linkage missing for " +
                            "isolated integration test running on CI server", ce);
            }
        }
        Assume.assumeTrue(success);
    }

    public boolean isContinuousIntegrationBuild() {
        return System.getenv("BUILD_NUMBER") != null;
    }
}
