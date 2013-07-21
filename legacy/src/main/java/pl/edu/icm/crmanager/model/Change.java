package pl.edu.icm.crmanager.model;

/**
 * Read-only change descriptor, a facade for {@link ChangeRequest}.
 *
 * @author mpol@icm.edu.pl
 */
public abstract class Change {
    protected ChangeRequest cr;

    Change(ChangeRequest request) {
        cr = request;
    }

    /**
     * @param getterName a getter method name
     * @return this change as a {@link ReferenceChange} if it refers to a reference property with the specified getter,
     *  <code>null</code> otherwise
     */
    public ReferenceChange referenceChangeForGetter(String getterName) {
        return null;
    }

    /**
     * Creates appropriate change descriptor for a change request.
     *
     * @param request a {@link ChangeRequest}
     * @return a {@link Change}, or <code>null</code> if the request is not supported
     */
    public static Change forRequest(ChangeRequest request) {
        if (request.getOldReferenceClass() != null || request.getNewReferenceClass() != null) {
            return new ReferenceChange(request);
        }
        return null;
    }
}
