package org.javers.core.metamodel.object;

/**
 * @author bartosz walacik
 */
public class OwnerContext {
    final   GlobalCdoId owner;
    final   String propertyName;
    private EnumeratorContext enumeratorContext;

    public OwnerContext(GlobalCdoId owner, String propertyName) {
        this.owner = owner;
        this.propertyName = propertyName;
    }

    public GlobalCdoId getGlobalCdoId() {
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
