package com.diffblue.deeptestutils.regression;

public abstract class AbstractBadStaticInit {

  static Integer i;
  static int j;

  static {
    j = i.intValue();
  }
}
