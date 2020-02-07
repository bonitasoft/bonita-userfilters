package org.bonitasoft.userfilter.test;

import static org.awaitility.Awaitility.await;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.identity.Group;
import org.bonitasoft.engine.identity.Role;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserMembership;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.test.junit.BonitaEngineRule;
import org.junit.Rule;

public class EngineTest {


    @Rule
    public BonitaEngineRule bonitaEngineRule = BonitaEngineRule.create();
    private APISession apiSession;


    ProcessDefinition deployProcess(BusinessArchive businessArchive) {
        return inTry(() -> getProcessAPI().deploy(businessArchive));
    }

    IdentityAPI getIdentityAPI() {
        return inTry(() -> TenantAPIAccessor.getIdentityAPI(apiSession));
    }

    ProcessAPI getProcessAPI() {
        return inTry(() -> TenantAPIAccessor.getProcessAPI(apiSession));
    }

    void loginOnDefaultTenantWithDefaultTechnicalUser() {
        loginOnDefaultTenantWith("install", "install");
    }

    void logoutOnTenant() {
        inTry(() -> TenantAPIAccessor.getLoginAPI().logout(apiSession));
    }

    void deleteUser(User user) {
        inTry(() -> getIdentityAPI().deleteUser(user.getId()));
    }

    void disableAndDeleteProcess(ProcessDefinition definition) {
        inTry(() -> {
            try {
                getProcessAPI().disableProcess(definition.getId());
            } catch (Exception ignored) {

            }
            getProcessAPI().deleteArchivedProcessInstances(definition.getId(), 0, 1000);
            getProcessAPI().deleteProcessInstances(definition.getId(), 0, 1000);
            getProcessAPI().deleteProcessDefinition(definition.getId());
        });
    }

    ActivityInstance waitForUserTask(ProcessInstance processInstance, String name) {
        return await().until(() ->
                getProcessAPI().getOpenActivityInstances(processInstance.getId(), 0, 100, ActivityInstanceCriterion.DEFAULT)
                        .stream()
                        .filter(a -> a.getName().equals(name))
                        .findFirst(), Optional::isPresent).get();
    }

    void loginOnDefaultTenantWith(String username, String password) {
        apiSession = inTry(() -> TenantAPIAccessor.getLoginAPI().login(username, password));
    }

    private <T> T inTry(Callable<T> callable) {
        try {

            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void inTry(RunnableWithException callable) {
        try {
            callable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Role createRole(String name) {
        return inTry(() -> getIdentityAPI().createRole(name));
    }

    Group createGroup(String name) {
        return inTry(() -> getIdentityAPI().createGroup(name, null));
    }

    User createUser(String username, String password) {
        return inTry(() -> getIdentityAPI().createUser(username, password));
    }

    void deleteUsers(User... users) {
        Arrays.stream(users).map(User::getId).forEach(u -> inTry(() -> getIdentityAPI().deleteUser(u)));
    }

    void deleteGroups(Group... groups) {
        inTry(() -> getIdentityAPI().deleteGroups(Arrays.stream(groups).map(Group::getId).collect(Collectors.toList())));
    }

    void deleteRoles(Role... roles) {
        inTry(() -> getIdentityAPI().deleteRoles(Arrays.stream(roles).map(Role::getId).collect(Collectors.toList())));

    }

    void deleteUserMemberships(UserMembership... memberships) {
        Arrays.stream(memberships).map(UserMembership::getId).forEach(u -> inTry(() -> getIdentityAPI().deleteUserMembership(u)));
    }

    private interface RunnableWithException {
        void run() throws Exception;
    }
}
