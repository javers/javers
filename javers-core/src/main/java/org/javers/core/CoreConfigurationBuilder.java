package org.javers.core;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.ListCompareAlgorithm;

import java.util.function.Supplier;

class CoreConfigurationBuilder {
    private PrettyValuePrinter prettyValuePrinter = PrettyValuePrinter.getDefault();

    private MappingStyle mappingStyle = MappingStyle.FIELD;

    private ListCompareAlgorithm listCompareAlgorithm = ListCompareAlgorithm.SIMPLE;

    private boolean newObjectsChanges = false;

    private CommitIdGenerator commitIdGenerator = CommitIdGenerator.SYNCHRONIZED_SEQUENCE;

    private Supplier<CommitId> customCommitIdGenerator;

    private CoreConfigurationBuilder() {
    }

    static CoreConfigurationBuilder coreConfiguration() {
        return new CoreConfigurationBuilder();
    }

    CoreConfiguration build() {
        return new CoreConfiguration(
                prettyValuePrinter,
                mappingStyle,
                listCompareAlgorithm,
                newObjectsChanges,
                commitIdGenerator,
                customCommitIdGenerator
                );
    }

    CoreConfigurationBuilder withMappingStyle(MappingStyle mappingStyle) {
        Validate.argumentIsNotNull(mappingStyle);
        this.mappingStyle = mappingStyle;
        return this;
    }

    CoreConfigurationBuilder withCommitIdGenerator(CommitIdGenerator commitIdGenerator) {
        Validate.argumentIsNotNull(commitIdGenerator);
        Validate.argumentCheck(commitIdGenerator != CommitIdGenerator.CUSTOM, "use withCustomCommitIdGenerator(Supplier<CommitId>)");
        this.commitIdGenerator = commitIdGenerator;
        this.customCommitIdGenerator = null;
        return this;
    }

    CoreConfigurationBuilder withCustomCommitIdGenerator(Supplier<CommitId> customCommitIdGenerator) {
        Validate.argumentIsNotNull(customCommitIdGenerator);
        this.commitIdGenerator = CommitIdGenerator.CUSTOM;
        this.customCommitIdGenerator = customCommitIdGenerator;
        return this;
    }

    CoreConfigurationBuilder withNewObjectsChanges(boolean newObjectsSnapshot) {
        this.newObjectsChanges = newObjectsSnapshot;
        return this;
    }

    CoreConfigurationBuilder withListCompareAlgorithm(ListCompareAlgorithm algorithm) {
        Validate.argumentIsNotNull(algorithm);
        this.listCompareAlgorithm = algorithm;
        return this;
    }

    CoreConfigurationBuilder withPrettyPrintDateFormats(JaversCoreProperties.PrettyPrintDateFormats prettyPrintDateFormats) {
        Validate.argumentIsNotNull(prettyPrintDateFormats);
        prettyValuePrinter = new PrettyValuePrinter(prettyPrintDateFormats);
        return this;
    }
}
