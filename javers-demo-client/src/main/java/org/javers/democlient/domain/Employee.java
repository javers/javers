package org.javers.democlient.domain;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author bartosz walacik
 */
public class Employee extends Person {

    public Employee(String login, String name, String surname, Sex sex) {
       super(login, name, surname, sex);
    }

    private Position position;
    private BigDecimal salary;
    private Person boss;
    private Set<Person> subordinates;

    public void fire(){
        position = null;
        salary = BigDecimal.ZERO;
        boss = null;
        subordinates = null;
    }

    public void assignPosition(Position position, BigDecimal salary){
        checkArgument(position != null);
        checkArgument(salary != null);
    }
}
