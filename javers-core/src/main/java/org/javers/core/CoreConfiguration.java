package org.javers.core;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.JaversCoreProperties.PrettyPrintDateFormats;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.ListCompareAlgorithm;

import java.util.function.Supplier;

/**
 * @author bartosz walacik
 */
public class CoreConfiguration {

    private final PrettyValuePrinter prettyValuePrinter;

    private final MappingStyle mappingStyle;

    private final ListCompareAlgorithm listCompareAlgorithm;

    private boolean prettyPrint;

    private final boolean newObjectChanges;

    private final boolean removedObjectChanges;

    private final CommitIdGenerator commitIdGenerator;

    private final Supplier<CommitId> customCommitIdGenerator;

    CoreConfiguration(PrettyValuePrinter prettyValuePrinter, MappingStyle mappingStyle, ListCompareAlgorithm listCompareAlgorithm, boolean newObjectChanges, CommitIdGenerator commitIdGenerator, Supplier<CommitId> customCommitIdGenerator, boolean removedObjectChanges, boolean prettyPrint) {
        this.prettyValuePrinter = prettyValuePrinter;
        this.mappingStyle = mappingStyle;
        this.listCompareAlgorithm = listCompareAlgorithm;
        this.newObjectChanges = newObjectChanges;
        this.commitIdGenerator = commitIdGenerator;
        this.customCommitIdGenerator = customCommitIdGenerator;
        this.removedObjectChanges = removedObjectChanges;
        this.prettyPrint = prettyPrint;
    }

    public PrettyValuePrinter getPrettyValuePrinter() {
        return prettyValuePrinter;
    }

    public MappingStyle getMappingStyle() {
        return mappingStyle;
    }

    public ListCompareAlgorithm getListCompareAlgorithm() {
        return listCompareAlgorithm;
    }

    public boolean isNewObjectChanges() {
        return newObjectChanges;
    }

    public boolean isRemovedObjectChanges() {
        return removedObjectChanges;
    }

    public CommitIdGenerator getCommitIdGenerator() {
        return commitIdGenerator;
    }

    public Supplier<CommitId> getCustomCommitIdGenerator() {
        return customCommitIdGenerator;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }
}
