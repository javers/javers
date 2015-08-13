package org.javers.test.dto;

import org.javers.core.diff.Diff;

import java.io.Serializable;

/**
 * Created by klearchos on 13/8/2015.
 */
public class SerializedWrapperDTO implements Serializable {

    /**
     * The Diff object should be serializable in order to assign it
     * into a serialized DTO that is usually required by the server's session.
     */
    private Diff diff;

    /**
     *
     * @return
     */
    public Diff getDiff() {
        return diff;
    }

    /**
     *
     * @param diff
     */
    public void setDiff(Diff diff) {
        this.diff = diff;
    }
}
