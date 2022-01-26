package org.javers.spring.boot.limit.domain;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.TypeName;
import org.javers.core.metamodel.annotation.ValueObject;

@Entity
@Table(name = "limitation_signature")
@TypeName("LimitSignature")
@ValueObject
public class LimitSignatureEntity implements Serializable {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @Column(name = "caption", nullable = false)
  private String caption;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JoinColumn(name = "limit_range_id")
  @DiffIgnore
  private LimitRangeEntity limitRange;

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

  public LimitRangeEntity getLimitRange() {
    return limitRange;
  }

  public void setLimitRange(LimitRangeEntity limitRange) {
    this.limitRange = limitRange;
  }
}
