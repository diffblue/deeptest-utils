package com.diffblue.deeptestutils;

// Copyright 2016-2018 Diffblue limited. All rights reserved.

/**
 * <code>DeeptestUtilsRuntimeException</code> is used to wrap other exceptions
 * thrown from Deeptest Utils that are not directly required for
 * the test.
 *
 * There are several exceptions that methods used in Deeptest Utils throw that
 * signify a problem with test generation itself or a change in the end user's
 * code base rather than an exception that can be handled in the test generated
 * by Deeptest. These exceptions should be caught and wrapped in a
 * <code>DeeptestUtilsRuntimeException</code> to avoid having to handle these
 * exceptions in the generated test. As an example, a
 * <code>ClassNotFoundException</code> would not be useful during test
 * generation and would not occur when the generated test is run under normal
 * circumstances, but the user might see one if they change the name of a class
 * in their code base that has old tests generated by Deeptest. Since the test
 * would not be able to handle this in a sensible way in the general case, we
 * catch this in Deeptest Utils and instead throw a
 * <code>DeeptestUtilsRuntimeException</code> at runtime with the information
 * contained in the original exception.
 *
 * The notable exception is the <code>InvocationTargetException</code>, which
 * is thrown by Deeptest Utils and handled in some test cases that use
 * reflection. This exception, and future exceptions that will be handled in
 * the generated test or by the Deeptest platform, must not be wrapped in a
 * <code>DeeptestUtilsRuntimeException</code>.
 *
 * @author <a href="http://diffblue.com">Diffblue</a>
 */
public class DeeptestUtilsRuntimeException extends RuntimeException {
  /**
   * Creates a new <code>DeeptestUtilsRuntimeException</code> instance with
   * message and cause.
   *
   * @param errorMsg Represents the error message. It contains the field
   * in question, the expected value and the actual value.
   * @param cause Represents the original exception.
   */
  DeeptestUtilsRuntimeException(final String errorMsg, final Throwable cause) {
    super(errorMsg, cause);
  }
}