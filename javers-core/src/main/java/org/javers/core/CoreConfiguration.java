package org.javers.core;

import org.javers.common.string.PrettyValuePrinter;
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

    private final boolean initialValueChanges;

    private final boolean terminalValueChanges;

    private final CommitIdGenerator commitIdGenerator;

    private final Supplier<CommitId> customCommitIdGenerator;

    CoreConfiguration(PrettyValuePrinter prettyValuePrinter, MappingStyle mappingStyle, ListCompareAlgorithm listCompareAlgorithm, boolean initialValueChanges, CommitIdGenerator commitIdGenerator, Supplier<CommitId> customCommitIdGenerator, boolean terminalValueChanges, boolean prettyPrint) {
        this.prettyValuePrinter = prettyValuePrinter;
        this.mappingStyle = mappingStyle;
        this.listCompareAlgorithm = listCompareAlgorithm;
        this.initialValueChanges = initialValueChanges;
        this.commitIdGenerator = commitIdGenerator;
        this.customCommitIdGenerator = customCommitIdGenerator;
        this.terminalValueChanges = terminalValueChanges;
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

    public boolean isInitialValueChanges() {
        return initialValueChanges;
    }

    public boolean isTerminalValueChanges() {
        return terminalValueChanges;
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
