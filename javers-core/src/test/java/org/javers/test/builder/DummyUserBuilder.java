package org.javers.test.builder;

import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;

import java.util.ArrayList;
import java.util.List;

import static org.javers.test.builder.DummyUserDetailsTestBuilder.dummyUserDetails;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class DummyUserBuilder {
    private static int DUMMY_ID = 0;
    private DummyUser dummyUser;

    private DummyUserBuilder() {
        dummyUser = new DummyUser();
    }

    public static DummyUserBuilder dummyUser() {
        return new DummyUserBuilder();
    }

    public DummyUser build() {
        if (dummyUser.getName() == null){
            dummyUser.setName("some");
        }
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
        List<DummyUser> employeesList = new ArrayList<>(numberOfEmployees);
        for(int i = 0; i < numberOfEmployees; i ++) {
            employeesList.add(new DummyUser("Em " + DUMMY_ID));
            DUMMY_ID ++;
        }
        dummyUser.setEmployeesList(employeesList);
        return this;
    }

}
