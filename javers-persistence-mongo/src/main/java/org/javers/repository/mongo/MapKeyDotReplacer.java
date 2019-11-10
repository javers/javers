package org.javers.repository.mongo;

import org.bson.Document;

import java.util.HashSet;

/**
 * @author bartosz.walacik
 */
class MapKeyDotReplacer {
    private static final String REPLACEMENT = "#dot#";

    Document replaceInSnapshotState(Document snapshot) {
        return replaceInPropertyMaps(snapshot, "\\.", ".", REPLACEMENT);
    }

    Document back(Document snapshot) {
        return replaceInPropertyMaps(snapshot, REPLACEMENT, REPLACEMENT, ".");
    }

    private Document replaceInPropertyMaps(Document snapshot, String regexFrom, String from, String to) {
        Document state = getState(snapshot);

        for (String pName : state.keySet()){
            if (state.get(pName) instanceof Document) {
                Document mapProperty = (Document)state.get(pName);
                state.put(pName, replaceInMapKeys(mapProperty, regexFrom, from, to));
            }
        }

        return snapshot;
    }

    private Document getState(Document snapshot) {
        return (Document) snapshot.get("state");
    }

    private Document replaceInMapKeys(Document map, String regexFrom, String from, String to) {
        for (String key : new HashSet<>(map.keySet())){
            if (key.contains(from)){
                String escaped = key.replaceAll(regexFrom, to);
                Object val = map.get(key);
                map.remove(key);
                map.put(escaped, val);
            }
        }
        return map;
    }
}
