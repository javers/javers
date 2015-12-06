package org.javers.core.json;

/**
 * @author bartosz.walacik
 */
public class GlobalIdRawDTO {
    private final String typeName;
    private final String localIdJSON;
    private final String fragment;
    private final GlobalIdRawDTO ownerId;

    public GlobalIdRawDTO(String typeName, String localIdJSON, String fragment, GlobalIdRawDTO ownerId) {
        this.typeName = typeName;
        this.localIdJSON = localIdJSON;
        this.fragment = fragment;
        this.ownerId = ownerId;
    }

    public boolean isInstanceId(){
        return localIdJSON != null;
    }

    public boolean isValueObjectId(){
        return ownerId != null;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getLocalIdJSON() {
        return localIdJSON;
    }

    public String getFragment() {
        return fragment;
    }

    public GlobalIdRawDTO getOwnerId() {
        return ownerId;
    }
}
