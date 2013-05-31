/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.userfilter.identity;

import java.util.Arrays;
import java.util.List;

import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.filter.AbstractUserFilter;
import org.bonitasoft.engine.filter.UserFilterException;

/**
 * @author Celine Souchet
 */
public class SingleUserUserFilter extends AbstractUserFilter {

    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        final Long userId = (Long) getInputParameter("userId");
        if (userId == null) {
            throw new ConnectorValidationException("The user identifier is null");
        }
        if (userId <= 0) {
            throw new ConnectorValidationException("The user identifier cannot be negative or equals to 0");
        }
    }

    @Override
    public List<Long> filter(final String actorName) throws UserFilterException {
        return Arrays.asList((Long) getInputParameter("userId"));
    }

}
