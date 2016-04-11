package org.javers.repository.sql

import org.javers.core.metamodel.annotation.Id
import org.joda.time.LocalDate

class MigrationEntity {

    @Id
    int id

    List<MigrationEntity> refs = []

    List<Integer> intList = [1, 2, 3, 4, 5, 6, 7, 8, 10]

    String str1 = "MigrationEntity.str1"
    String str2 = "MigrationEntity.str2"
    String str3 = "MigrationEntity.str3"
    String str4 = "MigrationEntity.str4"
    String str5 = "MigrationEntity.str5"

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

    static MigrationEntity produce(int startingId, int n){
        def root = new MigrationEntity(id:startingId)

        def range = startingId+1..startingId+n
        def children = range.collect{
            new MigrationEntity(id: it)
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
