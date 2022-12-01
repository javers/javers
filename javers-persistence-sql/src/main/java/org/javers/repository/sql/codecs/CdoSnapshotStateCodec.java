package org.javers.repository.sql.codecs;

/**
 * Minimalistic String-to-String codec interface for CdoSnapshotState SQL persistence.
 * Implementations may do anything from custom compression to application-layer encryption.
 */
public interface CdoSnapshotStateCodec {

    static CdoSnapshotStateCodec noop() {
        return NoOp.INSTANCE;
    }

    String encode(String plain);

    String decode(String encoded);

    /**
     * default pass-through implementation
     */
    class NoOp implements CdoSnapshotStateCodec {

        private static final NoOp INSTANCE = new NoOp();

        @Override
        public String encode(String plain) {
            return plain;
        }

        @Override
        public String decode(String encoded) {
            return encoded;
        }
    }
}
