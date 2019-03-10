package org.javers.core.metamodel.object;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;

public abstract class ValueObjectIdWithHash extends ValueObjectId {
    private static final String HASH_PLACEHOLDER = "{hashPlaceholder}";

    private ValueObjectIdWithHash(String typeName, GlobalId ownerId, String fragment) {
        super(typeName, ownerId, fragment);
    }

    public abstract boolean requiresHash();

    public abstract boolean hasHashOnParent();

    public abstract ValueObjectId freeze(String hash);

    public abstract ValueObjectId freeze();

    @Override
    public String toString() {
        return  getOwnerId().toString() +"#"+ getFragment() + " ("+this.getClass().getSimpleName()+")";
    }

    static class ValueObjectIdWithPlaceholder extends ValueObjectIdWithHash {
        private final String pathFromRoot;
        private String hash;

        ValueObjectIdWithPlaceholder(String typeName, GlobalId ownerId, String pathFromRoot) {
            super(typeName, ownerId, pathFromRoot + "/" + HASH_PLACEHOLDER);
            this.pathFromRoot = pathFromRoot;
            this.hash = HASH_PLACEHOLDER;
        }

        public ValueObjectId freeze(String hash) {
            if (!HASH_PLACEHOLDER.equals(this.hash)) {
                throw new JaversException(JaversExceptionCode.RUNTIME_EXCEPTION, "already frozen");
            }
            this.hash = hash;
            return new ValueObjectId(getTypeName(), getOwnerId(), this.getFragment());
        }

        @Override
        public boolean requiresHash() {
            return true;
        }

        @Override
        public boolean hasHashOnParent() {
            return false;
        }

        @Override
        public String getFragment() {
            return pathFromRoot + "/" + hash;
        }

        @Override
        public ValueObjectId freeze() {
            throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
        }
    }

    static class ValueObjectIdWithPlaceholderOnParent extends ValueObjectIdWithHash {
        private final ValueObjectIdWithHash parentId;
        private final String localPath;

        ValueObjectIdWithPlaceholderOnParent(String typeName, ValueObjectIdWithHash parentId, String localPath) {
            super(typeName, parentId.getOwnerId(), "{lazy}");

            this.parentId = parentId;
            this.localPath = localPath;
        }

        @Override
        public String getFragment() {
            return parentId.getFragment() + "/" + localPath;
        }

        @Override
        public boolean requiresHash() {
            return false;
        }

        @Override
        public boolean hasHashOnParent() {
            return true;
        }

        @Override
        public ValueObjectId freeze(String hash) {
            throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
        }

        @Override
        public ValueObjectId freeze() {
            return new ValueObjectId(getTypeName(), getOwnerId(), this.getFragment());
        }
    }
}