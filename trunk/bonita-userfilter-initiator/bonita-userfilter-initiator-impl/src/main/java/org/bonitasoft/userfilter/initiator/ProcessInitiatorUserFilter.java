/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.userfilter.initiator;

import java.util.Arrays;
import java.util.List;

import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.filter.AbstractUserFilter;
import org.bonitasoft.engine.filter.UserFilterException;

/**
 * @author Baptiste Mesta
 * @author Emmanuel Duchastenier
 */
public class ProcessInitiatorUserFilter extends AbstractUserFilter {

    @Override
    public void validateInputParameters() throws ConnectorValidationException {
    }

    @Override
    public List<Long> filter(final String actorName) throws UserFilterException {
        try {
            final long processInstanceId = getExecutionContext().getProcessInstanceId();
            return Arrays.asList(getAPIAccessor().getProcessAPI().getProcessInstance(processInstanceId).getStartedBy());
        } catch (final BonitaException e) {
            throw new UserFilterException(e);
        }
    }

    @Override
    public boolean shouldAutoAssignTaskIfSingleResult() {
        final Boolean autoAssignO = (Boolean) getInputParameter("autoAssign");
        return autoAssignO == null ? true : autoAssignO;
    }

}
