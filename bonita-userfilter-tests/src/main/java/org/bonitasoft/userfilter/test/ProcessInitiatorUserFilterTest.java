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
package org.bonitasoft.userfilter.test;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.bonitasoft.engine.BonitaSuiteRunner.Initializer;
import org.bonitasoft.engine.BonitaTestRunner;
import org.bonitasoft.engine.TestsInitializer;
import org.bonitasoft.engine.bpm.bar.BarResource;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.engine.expression.ExpressionBuilder;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.test.APITestUtil;
import org.bonitasoft.engine.test.wait.WaitForAssignedStep;
import org.bonitasoft.userfilter.initiator.ProcessInitiatorUserFilter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matthieu Chaffotte
 */
@RunWith(BonitaTestRunner.class)
@Initializer(TestsInitializer.class)
public class ProcessInitiatorUserFilterTest extends APITestUtil {

    @Test
    public void testProcessInitiatorUserFilterTest() throws Exception {
        final ExpressionBuilder expressionBuilder = new ExpressionBuilder();
        final String delivery = "Delivery men";

        final BusinessArchiveBuilder businessArchiveBuilder = new BusinessArchiveBuilder().createNewBusinessArchive();
        final ProcessDefinitionBuilder designProcessDefinition = new ProcessDefinitionBuilder().createNewInstance("ProcessWithAllConnector", "1.0");
        designProcessDefinition.addActor(delivery).addDescription("Delivery all day and night long");
        designProcessDefinition.addUserTask("step1", delivery).addUserFilter("initiator", "initiator", "1.0.0")
                .addInput("autoAssign", expressionBuilder.createConstantBooleanExpression(true));
        businessArchiveBuilder.setProcessDefinition(designProcessDefinition.done());

        final InputStream inputStream = ProcessInitiatorUserFilter.class.getResourceAsStream("/initiator-impl-1.0.0.impl");
        Assert.assertNotNull(inputStream);

        businessArchiveBuilder.addUserFilters(new BarResource("initiator-impl-1.0.0.impl", IOUtils.toByteArray(inputStream)));
        inputStream.close();

        login();
        final User matti = getIdentityAPI().createUser("matti", "bpm");
        final User aleksi = getIdentityAPI().createUser("aleksi", "bpm");
        final User juho = getIdentityAPI().createUser("juho", "bpm");

        final ProcessDefinition definition = getProcessAPI().deploy(businessArchiveBuilder.done());
        addMappingOfActorsForUser(delivery, matti.getId(), definition);
        addMappingOfActorsForUser(delivery, aleksi.getId(), definition);
        addMappingOfActorsForUser(delivery, juho.getId(), definition);
        getProcessAPI().enableProcess(definition.getId());
        logout();

        loginWith("matti", "bpm");
        final ProcessInstance processInstance = getProcessAPI().startProcess(definition.getId());
        final WaitForAssignedStep waitForAssignedStep = new WaitForAssignedStep(getProcessAPI(), "step1", processInstance.getId(), matti.getId());
        Assert.assertTrue(waitForAssignedStep.waitUntil());
        Assert.assertEquals(1, getProcessAPI().getAssignedHumanTaskInstances(matti.getId(), 0, 10, ActivityInstanceCriterion.NAME_DESC).size());
        logout();
        loginWith("aleksi", "bpm");
        Assert.assertEquals(0, getProcessAPI().getAssignedHumanTaskInstances(aleksi.getId(), 0, 10, ActivityInstanceCriterion.NAME_DESC).size());
        logout();
        loginWith("juho", "bpm");
        Assert.assertEquals(0, getProcessAPI().getAssignedHumanTaskInstances(juho.getId(), 0, 10, ActivityInstanceCriterion.NAME_DESC).size());
        logout();

        login();
        disableAndDeleteProcess(definition);
        deleteUser(matti);
        deleteUser(aleksi);
        deleteUser(juho);
        logout();
    }

}
