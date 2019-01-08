package com.diffblue.deeptestutils.regression;

public abstract class AbstractClass {

  abstract public int abstractMethod();

  public int concreteMethod() {
    return 23;
  }

  private String field;

  public void setField(String value) {
    field = value;
  }

  public void appendToField(String value) {
    field += value;
  }

  public String getField() {
    return field;
  }
}
