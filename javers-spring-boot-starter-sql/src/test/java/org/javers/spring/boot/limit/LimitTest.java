package org.javers.spring.boot.limit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javers.core.Javers;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.javers.spring.boot.TestApplication;
import org.javers.spring.boot.limit.domain.LimitEntity;
import org.javers.spring.boot.limit.domain.LimitRangeEntity;
import org.javers.spring.boot.limit.domain.LimitSignatureEntity;
import org.javers.spring.boot.limit.repository.LimitRangeRepository;
import org.javers.spring.boot.limit.repository.LimitRepository;
import org.javers.spring.boot.limit.repository.LimitSignatureRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(classes = TestApplication.class)
public class LimitTest {

  @Autowired
  private LimitRepository limitRepository;
  @Autowired
  private LimitRangeRepository limitRangeRepository;
  @Autowired
  private LimitSignatureRepository limitSignatureRepository;
  @Autowired
  private Javers javers;

  private static Set<LimitEntity> getLimits() {
    return IntStream.range(1, 3)
        .mapToObj(i -> {
          LimitEntity limitEntity = new LimitEntity();
          limitEntity.setCaption("LimitEntity " + i);
          return limitEntity;
        })
        .map(limitEntity -> {
          limitEntity.setLimitRanges(genLimitRanges(limitEntity));
          return limitEntity;
        })
        .collect(Collectors.toSet());
  }

  private static Set<LimitRangeEntity> genLimitRanges(LimitEntity limitEntity) {
    return IntStream.range(1, 3)
        .mapToObj(i -> {
          LimitRangeEntity limitRangeEntity = new LimitRangeEntity();
          limitRangeEntity.setLimit(limitEntity);
          limitRangeEntity.setCaption("LimitRangeEntity " + i);
          return limitRangeEntity;
        })
        .map(limitRangeEntity -> {
          limitRangeEntity.setLimitSignatures(genLimitSignatures(limitRangeEntity));
          return limitRangeEntity;
        })
        .collect(Collectors.toSet());
  }

  private static Set<LimitSignatureEntity> genLimitSignatures(LimitRangeEntity limitRangeEntity) {
    return IntStream.range(1, 3)
        .mapToObj(i -> {
          LimitSignatureEntity limitSignatureEntity = new LimitSignatureEntity();
          limitSignatureEntity.setLimitRange(limitRangeEntity);
          limitSignatureEntity.setCaption("LimitSignatureEntity " + i);
          return limitSignatureEntity;
        })
        .collect(Collectors.toSet());
  }

  @Test
  void should_test_npe() {
    limitRepository.saveAll(getLimits());
    assertEquals(2, limitRepository.count());
    assertEquals(4, limitRangeRepository.count());
    assertEquals(8, limitSignatureRepository.count());

    LimitEntity limitEntity = limitRepository.findAll()
        .get(0);
    LimitRangeEntity limitRangeEntity = limitEntity.getLimitRanges()
        .iterator()
        .next();
    LimitSignatureEntity limitSignatureEntity = limitRangeEntity.getLimitSignatures()
        .iterator()
        .next();

    //1 Change sub-sub child LimitSignatureEntity
    limitSignatureEntity.setCaption("1 Change");
    limitRepository.save(limitEntity);

    //2 Change sub child LimitRangeEntity
    limitRangeEntity.setCaption("2 Change");
    limitRepository.save(limitEntity);

    //3 Change parent LimitEntity
    limitEntity.setCaption("3 Change");
    limitRepository.save(limitEntity);

    //4 Change all
    limitSignatureEntity.setCaption("4 Change");
    limitRangeEntity.setCaption("4 Change");
    limitEntity.setCaption("4 Change");
    limitRepository.save(limitEntity);

    List<Shadow<LimitEntity>> shadowList = findHistory(limitEntity);
    assertEquals(3, shadowList.size());
  }

  private List<Shadow<LimitEntity>> findHistory(LimitEntity limitEntity) {
    JqlQuery queryBuilder = QueryBuilder.byInstanceId(limitEntity.getId(), LimitEntity.class)
        //.withChildValueObjects()
        //.withScopeDeepPlus()
        //.withScopeCommitDeep()
        .build();
    List<Shadow<LimitEntity>> shadowLimits = javers.findShadows(queryBuilder);
    return shadowLimits;
  }
}
