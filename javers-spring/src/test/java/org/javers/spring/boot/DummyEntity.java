package org.javers.spring.boot;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author bartosz.walacik
 */
@Entity
public class DummyEntity {
    @Id
    private String id;
    private String name;
}
