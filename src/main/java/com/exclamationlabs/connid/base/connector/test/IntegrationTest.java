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
public interface IntegrationTest {

    Log LOG = Log.getLog(IntegrationTest.class);

    String getConfigurationName();

    default void setup(Connector connector, Configuration configuration) {
        boolean success = false;
        try {
            configuration.validate();
            connector.init(configuration);
            success = true;
        } catch (ConfigurationException ce) {
            LOG.info("Integration test could not be run: " + ce.getMessage());
        }
        Assume.assumeTrue(success);
    }
}
