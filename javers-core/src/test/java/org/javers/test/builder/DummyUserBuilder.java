package org.javers.test.builder;

import com.google.common.collect.Lists;
import org.javers.common.collections.Sets;
import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.javers.core.model.DummyUser.*;
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails;

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

    public static DummyUserBuilder dummyUser(String name) {
        DummyUserBuilder builder = new DummyUserBuilder();
        return builder.withName(name);
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

    public DummyUserBuilder withInteger(Integer largeInt) {
        dummyUser.setLargeInt(largeInt);
        return this;
    }

    public DummyUserBuilder withSex(Sex sex) {
        dummyUser.setSex(sex);
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

    public DummyUserBuilder withFlag(boolean flag) {
        dummyUser.setFlag(flag);
        return this;
    }

    public DummyUserBuilder withValueMap(Map map) {
        dummyUser.setValueMap(map);
        return this;
    }

    public DummyUserBuilder withObjectMap(Map map) {
        dummyUser.setObjectMap(map);
        return this;
    }

    public DummyUserBuilder withPrimitiveMap(Map map) {
        dummyUser.setPrimitiveMap(map);
        return this;
    }

    public DummyUserBuilder withBoxedFlag(Boolean boxedFlag) {
        dummyUser.setBigFlag(boxedFlag);
        return this;
    }

    public DummyUserBuilder withAge(int age) {
        dummyUser.setAge(age);
        return this;
    }

    public DummyUserBuilder withStringsSet(String... strings) {
        dummyUser.setStringSet(Sets.asSet(strings));
        return this;
    }

    public DummyUserBuilder withIntegerList(Integer... integers) {
        dummyUser.setIntegerList(Lists.newArrayList(integers));
        return this;
    }
}
