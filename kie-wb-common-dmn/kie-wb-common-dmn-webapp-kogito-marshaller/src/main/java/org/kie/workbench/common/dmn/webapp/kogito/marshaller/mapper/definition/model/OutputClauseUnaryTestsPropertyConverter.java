/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import javax.xml.namespace.QName;

import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseUnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;

public class OutputClauseUnaryTestsPropertyConverter {

    public static OutputClauseUnaryTests wbFromDMN(final JSITUnaryTests dmn) {
        if (dmn == null) {
            return null;
        }
        final Id id = new Id(dmn.getId());
        final QName key = new QName(DMNModelInstrumentedBase.Namespace.KIE.getUri(),
                                    ConstraintType.CONSTRAINT_KEY,
                                    DMNModelInstrumentedBase.Namespace.KIE.getPrefix());
        final String constraintString = dmn.getOtherAttributes().getOrDefault(key, "");
        final ConstraintType constraint = ConstraintType.fromString(constraintString);
        final OutputClauseUnaryTests result = new OutputClauseUnaryTests(id,
                                                                         new Text(dmn.getText()),
                                                                         constraint);
        return result;
    }
}
