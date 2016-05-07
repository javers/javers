package org.javers.repository.jql

import org.javers.core.metamodel.annotation.Id
import org.joda.time.LocalDate

class NewPerformanceEntity {

    @Id
    int id

    List<NewPerformanceEntity> refs = []

    List<Integer> intList = [1, 2, 3, 4, 5, 6, 7, 8, 10]

    String str1 = "NewPerformanceEntity.str1"
    String str2 = "NewPerformanceEntity.str2"
    String str3 = "NewPerformanceEntity.str3"
    String str4 = "NewPerformanceEntity.str4"
    String str5 = "NewPerformanceEntity.str5"

    LocalDate date1 = new LocalDate(2001, 1, 1)
    LocalDate date2 = new LocalDate(2001, 1, 2)
    LocalDate date3 = new LocalDate(2001, 1, 3)
    LocalDate date4 = new LocalDate(2001, 1, 4)
    LocalDate date5 = new LocalDate(2001, 1, 5)

    MigrationValueObject vo = new MigrationValueObject(valueA: "test")
    MigrationValueObject anotherVo = new AnotherValueObject(valueA: "test", valueB:"test")

    def change() {
        intList.set(0, intList[0] + 1)

        vo.change()
        anotherVo.change()

        refs.each {
            it.change()
        }
    }

    static NewPerformanceEntity produce(int startingId, int n){
        def root = new NewPerformanceEntity(id:startingId)

        def range = startingId+1..startingId+n
        def children = range.collect{
            new NewPerformanceEntity(id: it)
        }

        root.refs = children
        root
    }
}

class MigrationValueObject {
    String valueA

    def change(){
        valueA += "a"
    }
}

class AnotherValueObject extends MigrationValueObject {
    String valueB
}
