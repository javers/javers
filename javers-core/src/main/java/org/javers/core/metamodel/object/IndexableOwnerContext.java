package org.javers.core.metamodel.object;

/**
 * Context for indexable properties: Lists & Arrays
 *
 * @author bartosz walacik
 */
public class IndexableOwnerContext extends OwnerContext {
    private int index;

    IndexableOwnerContext(GlobalCdoId owner, String propertyName) {
        super(owner, propertyName);
    }

    public IndexableOwnerContext(OwnerContext owner) {
        super(owner.getGlobalCdoId(), owner.getPropertyName());
    }

    @Override
    public String getPath() {
        return super.getPath()+"/"+index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
