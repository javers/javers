package org.javers.test.builder;

import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.spockframework.compiler.SpecParser;

import java.util.ArrayList;
import java.util.List;

import static org.javers.test.builder.DummyUserDetailsTestBuilder.dummyUserDetails;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class DummyUserBuilder {
    private int DUMMY_ID = 1;
    private DummyUser dummyUser;

    private DummyUserBuilder() {
        dummyUser = new DummyUser();
    }

    public static DummyUserBuilder dummyUser() {
        return new DummyUserBuilder();
    }

    public DummyUser build() {
        return dummyUser;
    }

    public DummyUserBuilder withName(String name) {
        dummyUser.setName(name);
        return this;
    }

    public DummyUserBuilder withSupervisor(String supervisorName) {
        dummyUser.setSupervisor(new DummyUser(supervisorName));
        return this;
    }

    public DummyUserBuilder withSupervisor(DummyUser supervisor) {
        dummyUser.setSupervisor(supervisor);
        return this;
    }


    public DummyUserBuilder withDetails() {
        dummyUser.setDummyUserDetails(dummyUserDetails().build());
        return this;
    }

    public DummyUserBuilder withDetails(long id) {
        dummyUser.setDummyUserDetails(dummyUserDetails().withId(id).build());
        return this;
    }

    public DummyUserBuilder withDetailsList(int numberOfDetailsInList) {

        List<DummyUserDetails> detailsList = new ArrayList<>(numberOfDetailsInList);
        for(int i = 0 ;i < numberOfDetailsInList ; i++) {
            detailsList.add(dummyUserDetails().withId(i + DUMMY_ID).build());
            DUMMY_ID++;
        }
        dummyUser.setDummyUserDetailsList(detailsList);
        return this;
    }
    public DummyUserBuilder withEmployees(int numberOfEmployees) {
        for(int i = 0; i < numberOfEmployees; i++) {
            dummyUser.addEmployee(new DummyUser("Em" + DUMMY_ID++));
        }
        return this;
    }

    public DummyUserBuilder withEmployee(DummyUser rob) {
        dummyUser.addEmployee(rob);
        return this;
    }
}
