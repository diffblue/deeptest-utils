package com.diffblue.deeptestutils.regression;

public class BadStaticInit {

  static Integer i;
  static int j;

  static {
    j = i.intValue();
  }
}
