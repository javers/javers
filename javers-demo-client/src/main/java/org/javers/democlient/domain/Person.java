package org.javers.democlient.domain;

import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author bartosz walacik
 */
public class Person {

    private String login;
    private String name;
    private String surname;
    private Sex sex;
    private Address address;

    public Person(String login, String name, String surname, Sex sex) {
        checkArgument(StringUtils.isNoneBlank(login));
        checkArgument(StringUtils.isNoneBlank(name));
        checkArgument(StringUtils.isNoneBlank(surname));
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.sex = sex;
    }
}
