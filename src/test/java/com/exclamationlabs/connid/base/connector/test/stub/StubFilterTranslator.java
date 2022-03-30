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

import org.identityconnectors.framework.common.objects.filter.*;

public class StubFilterTranslator extends AbstractFilterTranslator<AttributeFilter> {

    @Override
    protected AttributeFilter createEqualsExpression(EqualsFilter filter, boolean not) {
        if (filter == null || not) {
            return null;
        }
        return filter;
    }

    @Override
    protected AttributeFilter createContainsAllValuesExpression(ContainsAllValuesFilter filter, boolean not) {
        if (filter == null || not) {
            return null;
        }
        return filter;
    }


    @Override
    protected AttributeFilter createContainsExpression(ContainsFilter filter, boolean not) {
        if (filter == null || not) {
            return null;
        }
        return filter;
    }

}
