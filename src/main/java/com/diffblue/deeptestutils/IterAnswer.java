package com.diffblue.deeptestutils;

import org.mockito.stubbing.Answer;
import org.mockito.invocation.InvocationOnMock;
import java.util.ArrayList;

// Copyright 2016-2018 Diffblue limited. All rights reserved.

/**
 * <code>IterAnswer</code> provides a list of method return values calculated by
 * test-generator for use in mocking scenario.
 *
 * @param <T> type parameter for answer object
 * @author <a href="http://diffblue.com">Diffblue</a>
 */
public final class IterAnswer<T> implements Answer<T> {

  /**
   * Name of the class.
   */
  private String classname;

  /**
   * Name of the method.
   */
  private String methodname;

  /**
   * Index of the answer in sequence.
   */
  private int idx = 0;

  /**
   * List of answer objects.
   */
  private ArrayList<T> answers;

  /**
   * List of expected parameters to the function call.
   */
  private ArrayList<Object[]> expectedParameters;

  /**
   * Constructor.
   *
   * @param cn name of the class
   * @param mn name of the method
   * @param answerList list of answer objects
   * @param eps list of expected parameters
   */
  public IterAnswer(
    final String cn,
    final String mn,
    final ArrayList<T> answerList,
    final ArrayList<Object[]> eps) {
    classname = cn;
    methodname = mn;
    answers = answerList;
    expectedParameters = eps;
  }

  /**
   * This method is called on mocking to simulate a method invocation using the
   * expected parameters and the answers as calculated by the
   * test-generator. Also increases answer index for return values of next call.
   *
   * @param invocation mocking invocation object
   * @return list of expected parameters
   */
  public T answer(final InvocationOnMock invocation) {
    if (idx == answers.size()) {
        System.out.println("WARNING: more answers than in trace "
                           + (idx + 1) + " instead of just " + idx
                           + " will restart with first");
        idx = 0;
    }

    T result = answers.get(idx);

    if (!expectedParameters.isEmpty()) {
      Object[] expected = expectedParameters.get(idx);
      Object[] actual = invocation.getArguments();
      assert (expected.length == actual.length);
      for (int i = 0; i < expected.length; ++i) {
        try {
          CompareWithFieldList.compare(expected[i], actual[i]);
        } catch (UnexpectedValueException e) {
          String errormsg = "Mocked class " + classname
            + " method " + methodname + " invocation " + (idx + 1)
            + " parameter " + (i + 1) + ": " + e.getMessage();
          throw new UnexpectedValueException(errormsg);
        }
      }
    }
    idx++;
    return result;
  }
}
