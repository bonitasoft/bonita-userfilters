/**
 * Copyright (C) 2013, 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.userfilter.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bonitasoft.engine.bpm.flownode.ArchivedHumanTaskInstance;
import org.bonitasoft.engine.bpm.flownode.ArchivedHumanTaskInstanceSearchDescriptor;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.filter.AbstractUserFilter;
import org.bonitasoft.engine.filter.UserFilterException;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;

/**
 * Filters the userId(s) to the ones of the user(s) who executed a task specified by its name.
 * If a large number of task instances have the provided name for the running process, only the first 2000 results are returned.
 *
 * @author Emmanuel Duchastenier
 * @author Matthieu Chaffotte
 */
public class SameTaskUserUserFilter extends AbstractUserFilter {

    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        validateStringInputParameterIsNotNulOrEmpty("usertaskName");
    }

    @Override
    public List<Long> filter(final String actorName) throws UserFilterException {
        final String usertaskName = (String) getInputParameter("usertaskName");
        final SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0, 2000)
        .filter(ArchivedHumanTaskInstanceSearchDescriptor.PARENT_PROCESS_INSTANCE_ID, getExecutionContext().getProcessInstanceId())
        .filter(ArchivedHumanTaskInstanceSearchDescriptor.NAME, usertaskName).filter(ArchivedHumanTaskInstanceSearchDescriptor.TERMINAL, true);
        SearchResult<ArchivedHumanTaskInstance> searchResult;
        try {
            searchResult = getAPIAccessor().getProcessAPI().searchArchivedHumanTasks(searchOptionsBuilder.done());
        } catch (final SearchException e) {
            throw new UserFilterException("Problem searching for task named: " + usertaskName, e);
        }
        if (searchResult.getCount() == 0) {
            throw new UserFilterException("No finished task found with name: " + usertaskName);
        }
        final List<ArchivedHumanTaskInstance> tasks = searchResult.getResult();
        final List<Long> userIds = new ArrayList<Long>(tasks.size());
        for (final ArchivedHumanTaskInstance archivedTask : tasks) {
            final long executorId = archivedTask.getExecutedBy();
            if (!userIds.contains(executorId)) {
                userIds.add(executorId);
            }
        }
        return Collections.unmodifiableList(userIds);
    }

}
