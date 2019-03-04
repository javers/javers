package org.javers.core.metamodel.object;

public class ValueObjectIdWithHash extends ValueObjectId {
    private final String pathFromRoot;
    private String hash;

    private final ValueObjectIdWithHash parentId;
    private final String localPath;

    ValueObjectIdWithHash(String typeName, GlobalId ownerId, String pathFromRoot, String hash) {
        super(typeName, ownerId, "{lazy}");
        this.pathFromRoot = pathFromRoot;
        this.hash = hash;

        this.parentId = null;
        this.localPath = null;
    }

    ValueObjectIdWithHash(String typeName, ValueObjectIdWithHash parentId, String localPath) {
        super(typeName, parentId.getOwnerId(), "{lazy}");
        this.pathFromRoot = null;
        this.hash = null;

        this.parentId = parentId;
        this.localPath = localPath;
    }

    @Override
    public String toString() {
        return "VO Id with hash: " + getOwnerId().toString() +"#"+ getFragment() ;
    }

    @Override
    public String getFragment() {
        if (parentId != null) {
            return parentId.getFragment() + "/" + localPath;
        }
        return pathFromRoot + "/" + hash;
    }
}
//- ...Firewall/1#ingressRules/{hashPlaceholder}/port
