/**
 * Copyright (C) 2014 BonitaSoft S.A.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.flownode.ArchivedHumanTaskInstance;
import org.bonitasoft.engine.bpm.flownode.ArchivedHumanTaskInstanceSearchDescriptor;
import org.bonitasoft.engine.bpm.flownode.impl.internal.ArchivedUserTaskInstanceImpl;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.connector.EngineExecutionContext;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.filter.UserFilterException;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.search.impl.SearchOptionsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SameTaskUserUserFilterTest {

    private static final String HUMAN_TASK_NAME = "step1";

    private static final long PROCESS_INSTANCE_ID = 83L;

    @Mock
    private APIAccessor apiAccessor;

    @Mock
    private ProcessAPI processAPI;

    @Mock
    private SearchResult<ArchivedHumanTaskInstance> searchResult;

    private SameTaskUserUserFilter filter;

    @Before
    public void setUp() {
        filter = new SameTaskUserUserFilter();
        filter.setAPIAccessor(apiAccessor);
        when(apiAccessor.getProcessAPI()).thenReturn(processAPI);

        final EngineExecutionContext context = new EngineExecutionContext();
        context.setProcessInstanceId(PROCESS_INSTANCE_ID);
        filter.setExecutionContext(context);
    }

    private Map<String, Object> initParameters(final String humanTaskName) {
        return Collections.<String, Object> singletonMap("usertaskName", humanTaskName);
    }

    private List<ArchivedHumanTaskInstance> buildSingleResultList(final long userId) {
        final List<ArchivedHumanTaskInstance> result = new ArrayList<ArchivedHumanTaskInstance>();
        final ArchivedUserTaskInstanceImpl instanceImpl = new ArchivedUserTaskInstanceImpl(HUMAN_TASK_NAME);
        instanceImpl.setExecutedBy(4786L);
        result.add(instanceImpl);
        return result;
    }

    private List<ArchivedHumanTaskInstance> buildMultipleResultList(final long userId) {
        final List<ArchivedHumanTaskInstance> result = new ArrayList<ArchivedHumanTaskInstance>();
        ArchivedUserTaskInstanceImpl instanceImpl = new ArchivedUserTaskInstanceImpl(HUMAN_TASK_NAME);
        instanceImpl.setExecutedBy(4786L);
        result.add(instanceImpl);
        instanceImpl = new ArchivedUserTaskInstanceImpl(HUMAN_TASK_NAME);
        instanceImpl.setExecutedBy(4786L);
        result.add(instanceImpl);
        return result;
    }

    private SearchOptionsImpl buildSearchOptionsResult() {
        final SearchOptionsImpl optionsImpl = new SearchOptionsImpl(0, 2000);
        optionsImpl.addFilter(ArchivedHumanTaskInstanceSearchDescriptor.PARENT_PROCESS_INSTANCE_ID, PROCESS_INSTANCE_ID);
        optionsImpl.addFilter(ArchivedHumanTaskInstanceSearchDescriptor.NAME, HUMAN_TASK_NAME);
        optionsImpl.addFilter(ArchivedHumanTaskInstanceSearchDescriptor.TERMINAL, true);
        return optionsImpl;
    }

    @Test(expected = ConnectorValidationException.class)
    public void validateInputParameters_should_throw_an_exception_due_to_missing_human_task_name() throws Exception {
        filter.validateInputParameters();
    }

    @Test(expected = ConnectorValidationException.class)
    public void validateInputParameters_should_throw_an_exception_bacause_human_task_name_is_null() throws Exception {
        filter.setInputParameters(initParameters(null));

        filter.validateInputParameters();
    }

    @Test(expected = ConnectorValidationException.class)
    public void validateInputParameters_should_throw_an_exception_bacause_human_task_name_is_empty() throws Exception {
        filter.setInputParameters(initParameters("   "));

        filter.validateInputParameters();
    }

    @Test
    public void validateInputParameters_should_check_that_human_task_name_is_present() throws Exception {
        filter.setInputParameters(initParameters(HUMAN_TASK_NAME));

        filter.validateInputParameters();
    }

    @Test
    public void filter_should_return_the_list_of_user_ids() throws Exception {
        final long userId = 4786L;
        filter.setInputParameters(initParameters(HUMAN_TASK_NAME));
        when(processAPI.searchArchivedHumanTasks(any(SearchOptions.class))).thenReturn(searchResult);
        when(searchResult.getCount()).thenReturn(1L);
        final List<ArchivedHumanTaskInstance> result = buildSingleResultList(userId);
        when(searchResult.getResult()).thenReturn(result);
        final SearchOptionsImpl optionsImpl = buildSearchOptionsResult();

        final List<Long> userIds = filter.filter("Employee");

        assertThat(userIds).hasSize(1).contains(userId);
        verify(processAPI).searchArchivedHumanTasks(optionsImpl);
    }

    @Test
    public void filter_should_return_the_list_of_user_ids_without_duplicate() throws Exception {
        final long userId = 4786L;
        filter.setInputParameters(initParameters(HUMAN_TASK_NAME));
        when(processAPI.searchArchivedHumanTasks(any(SearchOptions.class))).thenReturn(searchResult);
        when(searchResult.getCount()).thenReturn(2L);
        final List<ArchivedHumanTaskInstance> result = buildMultipleResultList(userId);
        when(searchResult.getResult()).thenReturn(result);
        final SearchOptionsImpl optionsImpl = buildSearchOptionsResult();

        final List<Long> userIds = filter.filter("Employee");

        assertThat(userIds).hasSize(1).contains(userId);
        verify(processAPI).searchArchivedHumanTasks(optionsImpl);
    }

    @Test(expected = UserFilterException.class)
    public void filter_should_throw_an_exception_if_the_api_call_throws_an_exception() throws Exception {
        when(processAPI.searchArchivedHumanTasks(any(SearchOptions.class))).thenThrow(new SearchException(null));

        filter.filter("Employee");
    }

    @Test(expected = UserFilterException.class)
    public void filter_should_throw_an_exception_if_the_search_result_is_empty() throws Exception {
        when(processAPI.searchArchivedHumanTasks(any(SearchOptions.class))).thenReturn(searchResult);
        when(searchResult.getCount()).thenReturn(0L);

        filter.filter("Employee");
    }

}
