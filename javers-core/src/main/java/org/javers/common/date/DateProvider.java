package org.javers.common.date;

import org.javers.core.commit.CommitMetadata;
import java.time.ZonedDateTime;

/**
 * Date provider for {@link CommitMetadata#getCommitDate()}
 */
public interface DateProvider {
    ZonedDateTime now();
}
