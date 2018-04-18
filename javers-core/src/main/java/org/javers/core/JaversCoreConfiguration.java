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
public class JaversCoreConfiguration {

    private PrettyValuePrinter prettyValuePrinter = PrettyValuePrinter.getDefault();

    private MappingStyle mappingStyle = MappingStyle.FIELD;

    private ListCompareAlgorithm listCompareAlgorithm = ListCompareAlgorithm.SIMPLE;

    private boolean newObjectsSnapshot = false;

    private CommitIdGenerator commitIdGenerator = CommitIdGenerator.SYNCHRONIZED_SEQUENCE;

    private Supplier<CommitId> customCommitIdGenerator;

    JaversCoreConfiguration withMappingStyle(MappingStyle mappingStyle) {
        Validate.argumentIsNotNull(mappingStyle);
        this.mappingStyle = mappingStyle;
        return this;
    }

    JaversCoreConfiguration withCommitIdGenerator(CommitIdGenerator commitIdGenerator) {
        Validate.argumentIsNotNull(commitIdGenerator);
        Validate.argumentCheck(commitIdGenerator != CommitIdGenerator.CUSTOM, "use withCustomCommitIdGenerator(Supplier<CommitId>)");
        this.commitIdGenerator = commitIdGenerator;
        this.customCommitIdGenerator = null;
        return this;
    }

    JaversCoreConfiguration withCustomCommitIdGenerator(Supplier<CommitId> customCommitIdGenerator) {
        Validate.argumentIsNotNull(customCommitIdGenerator);
        this.commitIdGenerator = CommitIdGenerator.CUSTOM;
        this.customCommitIdGenerator = customCommitIdGenerator;
        return this;
    }

    JaversCoreConfiguration withNewObjectsSnapshot(boolean newObjectsSnapshot) {
        this.newObjectsSnapshot = newObjectsSnapshot;
        return this;
    }

    JaversCoreConfiguration withListCompareAlgorithm(ListCompareAlgorithm algorithm) {
        this.listCompareAlgorithm = algorithm;
        return this;
    }

    JaversCoreConfiguration withPrettyPrintDateFormats(PrettyPrintDateFormats prettyPrintDateFormats) {
        prettyValuePrinter = new PrettyValuePrinter(prettyPrintDateFormats);
        return this;
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

    public boolean isNewObjectsSnapshot() {
        return newObjectsSnapshot;
    }

    public CommitIdGenerator getCommitIdGenerator() {
        return commitIdGenerator;
    }

    public Supplier<CommitId> getCustomCommitIdGenerator() {
        return customCommitIdGenerator;
    }
}
