package org.javers.core.model;

import java.io.Serializable;

public class AbstractValueExample<T extends Serializable> implements Serializable {
  private Boolean option;
  private T value;
}
