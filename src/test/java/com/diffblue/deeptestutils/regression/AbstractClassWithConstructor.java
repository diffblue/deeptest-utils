package com.diffblue.deeptestutils.regression;

public abstract class AbstractClassWithConstructor {

  public abstract int abstractMethod();

  int field;

  public AbstractClassWithConstructor() {
    field = 100;
  }

  public int getField() {
    return field;
  }
}
