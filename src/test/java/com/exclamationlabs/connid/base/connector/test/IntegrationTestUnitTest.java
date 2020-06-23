package com.exclamationlabs.connid.base.connector.test;

import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationTestUnitTest extends IntegrationTest {

    private boolean isCI;

    @Mock
    protected Connector connector;

    @Mock
    protected Configuration configuration;

    @Override
    public String getConfigurationName() {
        return "testConfiguration";
    }

    @Test
    public void configurationName() {
        assertEquals("testConfiguration", getConfigurationName());
    }

    @Test
    public void setupForConnector() {
        setup(connector, configuration);
    }

    @Test
    public void setupForConfigurationOnly() {
        setup(configuration);
    }

    @Test // This should result in Ignored test via Assume
    public void setupForConnectorConfigurationProblem() {
        Mockito.doThrow(ConfigurationException.class)
                .when(connector)
                .init(configuration);
        setup(connector, configuration);
    }

    @Test // This should result in Ignored test via Assume
    public void setupForConfigurationProblem() {
        Mockito.doThrow(ConfigurationException.class)
                .when(configuration)
                .validate();
        setup(configuration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setupForConnectorConfigurationProblemCI() {
        isCI = true;
        Mockito.doThrow(ConfigurationException.class)
                .when(connector)
                .init(configuration);
        setup(connector, configuration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setupForConfigurationProblemCI() {
        isCI = true;
        Mockito.doThrow(ConfigurationException.class)
                .when(configuration)
                .validate();
        setup(configuration);
    }

    @Override
    public boolean isContinuousIntegrationBuild() {
        return isCI;
    }
}
