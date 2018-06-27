package com.diffblue.deeptestutils;

import java.util.ArrayList;

// Copyright 2016-2018 Diffblue limited. All rights reserved.

/**
 * <code>FieldList</code> represents a list of named fields and their values.
 *
 * @author <a href="http://diffblue.com">Diffblue</a>
 */
public class FieldList {

  /**
   * <code>ArrayList</code> of added <code>FieldValue</code>.
   *
   */
  private ArrayList<FieldValue> fields;

  /**
   * The fieldlist accessor.
   *
   * @return <code>ArrayList</code> of <code>FieldValue</code>
   */
  public final ArrayList<FieldValue> getFields() {
    return fields;
  }

  /**
   * Creates a new <code>FieldList</code> instance.
   *
   */
  public FieldList() {
    fields = new ArrayList<FieldValue>();
  }

  /**
   * <code>add</code> name / value pair to fieldlist.
   *
   * @param name the name of the field to add
   * @param value the value of the field to add
   */
  public final void add(final String name, final Object value) {
    fields.add(new FieldValue(name, value));
  }
}
