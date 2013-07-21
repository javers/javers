package pl.edu.icm.crmanager.model;

import pl.edu.icm.sedno.common.model.DataObject;

/**
 * A change descriptor for a reference change.
 *
 * @author mpol@icm.edu.pl
 */
public class ReferenceChange extends Change {

    ReferenceChange(ChangeRequest request) {
        super(request);
    }

    /**
     * @return identifier of the old referenced data object
     */
    public int getOldId() {
        return cr.getOldReferenceId();
    }

    /**
     * @return identifier of the new referenced data object
     */
    public int getNewId() {
        return cr.getNewReferenceId();
    }

    /**
     * @return class of the old referenced data object, or <code>null</code> if it was null
     */
    public Class<? extends DataObject> getOldClass() {
        return classOrNull(cr.getOldReferenceClass());
    }

    /**
     * @return class of the new referenced data object, or <code>null</code> if it is null
     */
    public Class<? extends DataObject> getNewClass() {
        return classOrNull(cr.getNewReferenceClass());
    }

    private static Class<? extends DataObject> classOrNull(String name) {
        try {
            if (name != null) {
                return (Class<? extends DataObject>) Class.forName(name);
            }
        } catch (ClassNotFoundException e) { /* fallthrough */ }
        return null;
    }

    @Override
    public ReferenceChange referenceChangeForGetter(String getterName) {
        return cr.getGetterName().equals(getterName) ? this : null;
    }
}
