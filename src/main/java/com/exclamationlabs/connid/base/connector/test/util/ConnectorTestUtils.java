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

import java.util.List;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ResultsHandler;

/** Utility methods to help simplify development of Connector unit/integration tests */
public class ConnectorTestUtils {

  private ConnectorTestUtils() {}

  public static ResultsHandler buildResultsHandler(List<String> idValues, List<String> nameValues) {
    return (ConnectorObject connectorObject) -> {
      idValues.add(connectorObject.getUid().getUidValue());
      nameValues.add(connectorObject.getName().getNameValue());
      return true;
    };
  }

  public static ResultsHandler buildResultsHandler(List<String> idValues, List<String> nameValues, List<ConnectorObject> results) {
    return (ConnectorObject connectorObject) -> {
      idValues.add(connectorObject.getUid().getUidValue());
      nameValues.add(connectorObject.getName().getNameValue());
      results.add(connectorObject);
      return true;
    };
  }
}
