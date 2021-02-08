package org.javers.core.metamodel.type

import org.javers.core.MappingStyle
import spock.lang.Shared
import spock.lang.Specification

class TypeFactoryBeanTest extends Specification {
    @Shared TypeFactory typeFactory

    def setupSpec() {
        typeFactory = TypeFactoryTest.create(MappingStyle.BEAN)
    }

    interface ISimplePojo {
        int getName()

        void setName(int name)
    }

    interface IExtendedPojo extends ISimplePojo {
        int getAnotherName()

        void setAnotherName(int anotherName)
    }

    class SimplePojo implements ISimplePojo {
        int name;

        @Override
        int getName() {
            return name
        }

        @Override
        void setName(int name) {
            this.name = name
        }
    }

    class ExtendedPojo extends SimplePojo implements IExtendedPojo {
        int anotherName

        int getAnotherName() {
            return anotherName
        }

        void setAnotherName(int anotherName) {
            this.anotherName = anotherName
        }
    }

    def "should not duplicate a property when inherited from superclass and from interface" () {
        when:
        def jType = typeFactory.infer(ExtendedPojo)
        println jType.prettyPrint()

        then:
        jType.properties.size() == 2
        jType.getProperty("anotherName")
        jType.getProperty("name")
    }
}
