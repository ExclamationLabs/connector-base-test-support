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

package com.exclamationlabs.connid.base.connector.test.stub;

import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.AttributeFilter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class StubConnector implements PoolableConnector, SchemaOp, TestOp,
        DeleteOp, CreateOp, UpdateDeltaOp, SearchOp<AttributeFilter> {

    @Override
    public Configuration getConfiguration() {
        return new StubConfiguration();
    }

    @Override
    public void init(Configuration cfg) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void checkAlive() {

    }

    @Override
    public Schema schema() {
        return null;
    }

    @Override
    public void test() {

    }

    @Override
    public Uid create(ObjectClass objectClass, Set<Attribute> createAttributes, OperationOptions options) {
        Random random = new Random();
        int idValue = Math.abs(random.nextInt());
        return new Uid("" + idValue);
    }

    @Override
    public void delete(ObjectClass objectClass, Uid uid, OperationOptions options) {

    }

    @Override
    public FilterTranslator<AttributeFilter> createFilterTranslator(ObjectClass objectClass, OperationOptions options) {
        return new StubFilterTranslator();
    }

    @Override
    public void executeQuery(ObjectClass objectClass, AttributeFilter query, ResultsHandler handler, OperationOptions options) {

        if (query == null) {
            handler.handle(getConnectorObjectBuilder(objectClass).build());
            handler.handle(getConnectorObjectBuilder(objectClass).build());
        } else {
            handler.handle(getConnectorObjectBuilder(query, objectClass).build());
        }
    }

    @Override
    public Set<AttributeDelta> updateDelta(ObjectClass objclass, Uid uid, Set<AttributeDelta> modifications, OperationOptions options) {
        return new HashSet<>();
    }

    protected final ConnectorObjectBuilder getConnectorObjectBuilder(ObjectClass objectClass) {
        Random random = new Random();
        int idValue = Math.abs(random.nextInt());
        return new ConnectorObjectBuilder()
                .setObjectClass(objectClass)
                .setUid("" + idValue)
                .setName("NAME: " + idValue);
    }

    protected final ConnectorObjectBuilder getConnectorObjectBuilder(AttributeFilter query, ObjectClass objectClass) {
        return new ConnectorObjectBuilder()
                .setObjectClass(objectClass)
                .setUid(query.getAttribute().getValue().get(0).toString())
                .setName("NAME: " + query.getAttribute().getValue().get(0).toString());
    }
}
