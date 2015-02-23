package org.javers.repository.sql;

import org.javers.common.collections.Arrays;
import org.javers.common.collections.Lists;
import org.joda.time.LocalDate;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * class for performance tests
 * @author bartosz walacik
 */
class PerformanceEntity {

    @Id
    int id;

    List<PerformanceEntity> refs = new ArrayList<>();

    List<Integer> intList = Lists.asList(1, 2, 3, 4, 5, 6, 7, 8, 10);

    String str1 = "PerformanceEntity.str1";
    String str2 = "PerformanceEntity.str2";
    String str3 = "PerformanceEntity.str3";
    String str4 = "PerformanceEntity.str4";
    String str5 = "PerformanceEntity.str5";

    LocalDate date1 = new LocalDate(2001, 1, 1);
    LocalDate date2 = new LocalDate(2001, 1, 2);
    LocalDate date3 = new LocalDate(2001, 1, 3);
    LocalDate date4 = new LocalDate(2001, 1, 4);
    LocalDate date5 = new LocalDate(2001, 1, 5);

    void change() {
        intList.set(0, intList.get(0) + 1);

        for (PerformanceEntity ref : refs) {
            ref.change();
        }
    }
}
