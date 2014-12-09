package org.javers.core.changelog;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Sample text changeLog, renders text log like that:
 * <pre>
 * commit 3.0, author:another author, 2014-12-06 13:22:51
 *   changed object: org.javers.core.model.DummyUser/bob
 *     value changed on 'sex' property: 'null' -> 'FEMALE'
 *     set changed on 'stringSet' property: [removed:'groovy', added:'java', added:'scala']
 *     list changed on 'integerList' property: [(0).added:'22', (1).added:'23']
 * commit 2.0, author:some author, 2014-12-06 13:22:51
 *     value changed on 'age' property: '0' -> '18'
 *     value changed on 'surname' property: 'Dijk' -> 'van Dijk'
 *     reference changed on 'supervisor' property: 'null' -> 'org.javers.core.model.DummyUser/New Supervisor'
 * </pre>
 *
 * @author bartosz walacik
 */
public class SimpleTextChangeLog extends AbstractTextChangeLog {
    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormat.mediumDateTime();

    private final DateTimeFormatter dateTimeFormatter;

    public SimpleTextChangeLog() {
        this(DEFAULT_DATE_FORMATTER);
    }

    public SimpleTextChangeLog(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public void onCommit(CommitMetadata commitMetadata) {
        appendln("commit " + commitMetadata.getId() + ", author:" + commitMetadata.getAuthor() +
                ", " + DEFAULT_DATE_FORMATTER.print(commitMetadata.getCommitDate()));
    }

    @Override
    public void onAffectedObject(GlobalId globalId) {
        appendln("  changed object: " + globalId.value());
    }

    @Override
    public void onValueChange(ValueChange valueChange) {
        appendln("    value changed on '"+valueChange.getProperty().getName()+"' property: '"+ valueChange.getLeft() +
                 "' -> '" + valueChange.getRight() + "'");
    }

    @Override
    public void onReferenceChange(ReferenceChange referenceChange) {
        appendln("    reference changed on '" + referenceChange.getProperty().getName() + "' property: '" + referenceChange.getLeft() +
                "' -> '" + referenceChange.getRight() + "'");
    }

    @Override
    public void onNewObject(NewObject newObject) {
        appendln("    new object: '" + newObject.getAffectedGlobalId());
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
        appendln("    object removed: '" + objectRemoved.getAffectedGlobalId());
    }

    @Override
    public void onMapChange(MapChange mapChange) {
        appendln("    map changed on '" + mapChange.getProperty().getName() + "' property: " +
                mapChange.getEntryChanges());
    }

    @Override
    public void onArrayChange(ArrayChange arrayChange) {
        appendln("    array changed on '" + arrayChange.getProperty().getName() + "' property: " +
                arrayChange.getChanges());
    }

    @Override
    public void onListChange(ListChange listChange) {
        appendln("    list changed on '" + listChange.getProperty().getName() + "' property: " +
                listChange.getChanges());
    }

    @Override
    public void onSetChange(SetChange setChange) {
        appendln("    set changed on '" + setChange.getProperty().getName() + "' property: "+
                 setChange.getChanges());
    }

}
