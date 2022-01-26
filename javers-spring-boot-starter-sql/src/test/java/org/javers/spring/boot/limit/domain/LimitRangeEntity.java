package org.javers.spring.boot.limit.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.TypeName;
import org.javers.core.metamodel.annotation.ValueObject;

@Entity
@Table(name = "limitation_range")
@TypeName("LimitRange")
@ValueObject
public class LimitRangeEntity implements Serializable {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @Column(name = "caption", nullable = false)
  private String caption;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JoinColumn(name = "limit_id")
  @DiffIgnore
  private LimitEntity limit;

  @OneToMany(mappedBy = "limitRange", orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<LimitSignatureEntity> limitSignatures = new HashSet<>();

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public LimitEntity getLimit() {
    return limit;
  }

  public void setLimit(LimitEntity limit) {
    this.limit = limit;
  }

  public Set<LimitSignatureEntity> getLimitSignatures() {
    return limitSignatures;
  }

  public void setLimitSignatures(Set<LimitSignatureEntity> limitSignatures) {
    this.limitSignatures = limitSignatures;
  }
}
