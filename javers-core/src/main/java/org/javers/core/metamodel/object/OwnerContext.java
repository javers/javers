package org.javers.core.metamodel.object;

/**
 * @author bartosz walacik
 */
public class OwnerContext {
    final   GlobalId owner;
    final   String propertyName;
    private EnumeratorContext enumeratorContext;

    public OwnerContext(GlobalId owner, String propertyName) {
        this.owner = owner;
        this.propertyName = propertyName;
    }

    public GlobalId getGlobalId() {
        return owner;
    }

    public String getPath() {
        return propertyName  + getEnumeratorContextPath();
    }

    public void setEnumeratorContext(EnumeratorContext enumeratorContext) {
        this.enumeratorContext = enumeratorContext;
    }

    public <T extends EnumeratorContext> T getEnumeratorContext() {
        return (T)enumeratorContext;
    }

    String getEnumeratorContextPath(){
        if (enumeratorContext == null){
            return "";
        }
        return "/"+enumeratorContext.getPath();
    }
}
