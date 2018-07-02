package com.diffblue.deeptestutils;

// Copyright 2016-2018 Diffblue limited. All rights reserved.

/**
 * <code>FieldValue</code> represents a named field and its value.
 *
 * @author <a href="http://diffblue.com">Diffblue</a>
 */
class FieldValue {
  /**
   * Creates a new <code>FieldValue</code> instance.
   *
   * @param pname the name of the field
   * @param pvalue the value of the field
   */
  FieldValue(final String pname, final Object pvalue) {
    name = pname;
    value = pvalue;
  }

  /**
   * Name of the field.
   *
   */
  private String name;

  /**
   * The name accessor.
   *
   * @return the name of the field
   */
  String getName() {
    return name;
  }

  /**
   * Value of the field.
   *
   */
  private Object value;

  /**
   * The value accessor.
   *
   * @return the value of the field
   */
  Object getValue() {
    return value;
  }
}
