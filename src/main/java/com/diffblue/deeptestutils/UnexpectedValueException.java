package com.diffblue.deeptestutils;

// Copyright 2016-2018 Diffblue limited. All rights reserved.

/**
 * <code>UnexpectedValueException</code> is used to signal an expectation
 * failure within <code>CompareWithFieldLists</code>, which deep-compares
 * expected objects with FieldLists or primitives indicating their expected
 * field values. For example, <code>CompareWithFieldLists.compare(1, 2);</code>
 * will yield this exception, as will <code>compare</code>'ing an object with
 * field <code>x</code> = 1 against a FieldList witih <code>x</code> = 2.
 *
 * @author <a href="http://diffblue.com">Diffblue</a>
 */
public class UnexpectedValueException extends RuntimeException {
  /**
   * Creates a new <code>UnexpectedValueException</code> instance.
   *
   * @param errorMsg Represents the error message. It contains the field in
   *     question, the expected value and the actual value.
   */
  UnexpectedValueException(final String errorMsg) {
    super(errorMsg);
  }
}
