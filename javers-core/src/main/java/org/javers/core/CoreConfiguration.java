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

    private final boolean newObjectsChanges;

    private final CommitIdGenerator commitIdGenerator;

    private final Supplier<CommitId> customCommitIdGenerator;

    CoreConfiguration(PrettyValuePrinter prettyValuePrinter, MappingStyle mappingStyle, ListCompareAlgorithm listCompareAlgorithm, boolean newObjectsChanges, CommitIdGenerator commitIdGenerator, Supplier<CommitId> customCommitIdGenerator) {
        this.prettyValuePrinter = prettyValuePrinter;
        this.mappingStyle = mappingStyle;
        this.listCompareAlgorithm = listCompareAlgorithm;
        this.newObjectsChanges = newObjectsChanges;
        this.commitIdGenerator = commitIdGenerator;
        this.customCommitIdGenerator = customCommitIdGenerator;
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

    public boolean isNewObjectsChanges() {
        return newObjectsChanges;
    }

    public CommitIdGenerator getCommitIdGenerator() {
        return commitIdGenerator;
    }

    public Supplier<CommitId> getCustomCommitIdGenerator() {
        return customCommitIdGenerator;
    }
}
