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

import com.exclamationlabs.connid.base.connector.test.stub.StubConfiguration;
import com.exclamationlabs.connid.base.connector.test.stub.StubConnector;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ApiIntegrationTestUnitTest extends
        ApiIntegrationTest<StubConfiguration, StubConnector> {

    private boolean configurationRead;

    @Before
    public void setup() {
        super.setup();
        configurationRead = true;
    }

    @Override
    protected StubConfiguration getConfiguration() {
        return new StubConfiguration();
    }

    @Override
    protected Class<StubConnector> getConnectorClass() {
        return StubConnector.class;
    }

    @Override
    protected void readConfiguration(StubConfiguration configurationObject) {
        configurationRead = true;
    }

    @Test
    public void configurationNotNull() {
        assertNotNull(getConfiguration());
    }

    @Test
    public void connectorClassValid() {
        assertNotNull(getConnectorClass());
    }

    @Test
    public void configurationRead() {
        assertTrue(configurationRead);
    }

    @Test
    public void testMethod() {
        connectorFacade.test();
    }

    @Test
    public void getAll() {
        getConnectorFacade().search( ObjectClass.ACCOUNT, null, handler, new OperationOptionsBuilder().build());
        assertEquals(2, results.size());
    }

    @Test
    public void getOne() {
        Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue("test").build();
        getConnectorFacade().search( ObjectClass.ACCOUNT, new EqualsFilter(idAttribute), handler, new OperationOptionsBuilder().build());
        assertEquals(1, results.size());
        assertTrue(StringUtils.isNotBlank(results.get(0).getUid().getUidValue()));
        assertTrue(StringUtils.isNotBlank(results.get(0).getUid().getName()));
    }

    @Test
    public void create() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName("testme").addValue("testvalue").build());
        Uid newId = connectorFacade.create(ObjectClass.ACCOUNT, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test
    public void modify() {
        Set<AttributeDelta> attributes = new HashSet<>();
        attributes.add(new AttributeDeltaBuilder().setName(
                "dummy").addValueToReplace("dummy data").build());

        Set<AttributeDelta> response = connectorFacade.updateDelta(ObjectClass.ACCOUNT, new Uid("dummy id"),
                attributes, new OperationOptionsBuilder().build());

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    public void delete() {
        connectorFacade.delete(ObjectClass.ACCOUNT, new Uid("dummy id"), new OperationOptionsBuilder().build());
    }

}
