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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.javers.core.metamodel.annotation.TypeName;

@Entity
@Table(name = "limitation")
@TypeName("Limit")
public class LimitEntity implements Serializable {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @Column(name = "caption", nullable = false)
  private String caption;

  @OneToMany(mappedBy = "limit", orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<LimitRangeEntity> limitRanges = new HashSet<>();

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

  public Set<LimitRangeEntity> getLimitRanges() {
    return limitRanges;
  }

  public void setLimitRanges(Set<LimitRangeEntity> limitRanges) {
    this.limitRanges = limitRanges;
  }
}
