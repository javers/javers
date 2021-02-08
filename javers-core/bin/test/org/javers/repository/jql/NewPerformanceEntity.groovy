package org.javers.repository.jql

import org.javers.core.metamodel.annotation.Id
import java.time.LocalDate

class NewPerformanceEntity {

    @Id
    int id

    List<NewPerformanceEntity> refs = []

    List<Integer> intList = new ArrayList((1..100))

    String str1 = "NewPerformanceEntity.str1"
    String str2 = "NewPerformanceEntity.str2"
    String str3 = "NewPerformanceEntity.str3"
    String str4 = "NewPerformanceEntity.str4"
    String str5 = "NewPerformanceEntity.str5"

    LocalDate date1 = LocalDate.of(2001, 1, 1)
    LocalDate date2 = LocalDate.of(2001, 1, 2)
    LocalDate date3 = LocalDate.of(2001, 1, 3)
    LocalDate date4 = LocalDate.of(2001, 1, 4)
    LocalDate date5 = LocalDate.of(2001, 1, 5)

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

    static NewPerformanceEntity produce(int childrenCnt){
        def root = new NewPerformanceEntity(id: UUID.randomUUID().hashCode())

        def range = 1..childrenCnt
        def children = range.collect{
            new NewPerformanceEntity(id: UUID.randomUUID().hashCode())
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
