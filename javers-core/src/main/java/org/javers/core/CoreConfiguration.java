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

    private final boolean initialChanges;

    private final boolean terminalChanges;
    private final boolean terminalSnapshot;

    private final boolean usePrimitiveDefaults;

    private final CommitIdGenerator commitIdGenerator;

    private final Supplier<CommitId> customCommitIdGenerator;

    CoreConfiguration(PrettyValuePrinter prettyValuePrinter, MappingStyle mappingStyle, ListCompareAlgorithm listCompareAlgorithm, boolean initialChanges, CommitIdGenerator commitIdGenerator, Supplier<CommitId> customCommitIdGenerator, boolean terminalChanges, boolean terminalSnapshot, boolean prettyPrint,
            boolean usePrimitiveDefaults) {
        this.prettyValuePrinter = prettyValuePrinter;
        this.mappingStyle = mappingStyle;
        this.listCompareAlgorithm = listCompareAlgorithm;
        this.initialChanges = initialChanges;
        this.commitIdGenerator = commitIdGenerator;
        this.customCommitIdGenerator = customCommitIdGenerator;
        this.terminalChanges = terminalChanges;
        this.terminalSnapshot = terminalSnapshot;
        this.prettyPrint = prettyPrint;
        this.usePrimitiveDefaults = usePrimitiveDefaults;
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

    public boolean isInitialChanges() {
        return initialChanges;
    }

    public boolean getUsePrimitiveDefaults() {
        return usePrimitiveDefaults;
    }

    public boolean isTerminalChanges() {
        return terminalChanges;
    }

    public boolean isTerminalSnapshot() { return terminalSnapshot; }

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
