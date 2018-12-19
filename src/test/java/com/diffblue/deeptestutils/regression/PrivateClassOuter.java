package com.diffblue.deeptestutils.regression;

public class PrivateClassOuter {

  private class PrivateClass {
    int field;
  }

  private class PrivateImplementingClass implements Interface {
    public int method() {
      return 11;
    }
  }

}
