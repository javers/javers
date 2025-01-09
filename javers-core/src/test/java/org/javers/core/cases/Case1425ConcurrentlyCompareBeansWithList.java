package org.javers.core.cases;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.junit.jupiter.api.Test;

/**
 * https://github.com/javers/javers/issues/1425
 *
 * @author Adrien-dev25
 */
public class Case1425ConcurrentlyCompareBeansWithList {

    static class BeanAWithList {
        List<String> items;
    }

    static class BeanBWithList {
        List<String> items;
    }

    @Test
    public void shouldNotThrowExceptionWhenConcurrentlyComparingBeanWithListProperties() {
        //given
        Javers javers = JaversBuilder.javers().build();

        //when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                futures.add(CompletableFuture.runAsync(() -> javers.compare(new BeanAWithList(), new BeanAWithList()),
                        executorService));
                futures.add(CompletableFuture.runAsync(() -> javers.compare(new BeanBWithList(), new BeanBWithList()),
                        executorService));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            fail("Should not throw ATTEMPT_TO_OVERWRITE_EXISTING_JAVERSTYPE_MAPPING exception.", e);
        } finally {
            executorService.shutdown();
        }
    }
}
