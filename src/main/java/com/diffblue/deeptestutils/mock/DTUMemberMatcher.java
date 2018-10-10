// Copyright 2016-2018 Diffblue limited. All rights reserved.

package com.diffblue.deeptestutils.mock;

import java.lang.reflect.Method;

import org.powermock.reflect.exceptions.TooManyMethodsFoundException;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.reflect.internal.comparator.ComparatorFactory;

import java.util.Arrays;
import java.util.Comparator;

/**
 * <code>DTUMemberMatcher</code> is a utility class for matching members of
 * classes.
 * <p>
 * We current only use it to match methods, as a replacement for
 * org.powermock.api.support.membermodification.MemberMatcher.method
 * which can trigger a `TooManyMethodsFoundException` in some cases.
 * This implements TG-5254.
 *
 * @author <a href="http://diffblue.com">Diffblue</a>
 */
public final class DTUMemberMatcher {

  /**
   * Private constructor to prevent instantiation of the utility class.
   */
  private DTUMemberMatcher() {
  }

  /**
   * Get a method matching the given name and parameter types. If there
   * are multiple methods then pick the one that would be chosen by Java at
   * runtime.
   * <p>
   * We first call the MemberMatcher of PowerMock to try and find a
   * matching method. We return its result unless it throws a
   * TooManyMethodsFoundException exception. In that case, we catch the
   * exception and look for the method ourselves.
   * <p>
   * Java specification for method invocation:
   * https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.12
   *
   * @param declaringClass The class to search
   * @param methodName     The name of the desired method
   * @param parameterTypes Types that can be fed as arguments to the method
   *                       (can be more specific than the method's declared
   *                       types)
   * @return A matching method if such exists
   * @throws MethodNotFoundException If a method cannot be found in the
   *                                 hierarchy.
   */
  public static Method method(final Class<?> declaringClass,
                              final String methodName,
                              final Class<?>... parameterTypes) {

    try {
      return org.powermock.api.support.membermodification.MemberMatcher
          .method(declaringClass, methodName, parameterTypes);
    } catch (TooManyMethodsFoundException e) {
      return methodUsingWholeSignature(declaringClass, methodName,
          parameterTypes);
    }
  }

  /**
   * Get a method matching the given name and parameter types. If there
   * are multiple methods then pick the one that would be chosen by Java at
   * runtime.
   * <p>
   * We first gather all methods from the class and its ancestors that can
   * be considered as a match and then find the best match using
   * MethodComparatorFull.
   * <p>
   * Note that this does not handle generics, variadic arguments, arrays and
   * possibly other cases correctly. For full spec on method invocation see
   * https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.12
   * <p>
   * This is a helper method that is not intended to be called by users,
   * instead use the "method" method.
   *
   * @param declaringClass The class to search
   * @param methodName     The name of the desired method
   * @param parameterTypes Types that can be fed as arguments to the method
   *                       (can be more specific than the method's declared
   *                       types)
   * @return A matching method if such exists
   */
  protected static Method methodUsingWholeSignature(
      final Class<?> declaringClass,
      final String methodName,
      final Class<?>... parameterTypes) {

    Method[] declaredMethods = WhiteboxImpl.getMethods(
        declaringClass, methodName, parameterTypes, false);
    Arrays.sort(declaredMethods, new MethodComparatorFull());

    if (declaredMethods.length == 0) {
      WhiteboxImpl.throwExceptionIfMethodWasNotFound(declaringClass,
          methodName, null, new Object[]{parameterTypes});
      return null;
    } else {
      return declaredMethods[0];
    }
  }

}

/**
 * Comparator for methods that determines which method is more specific. We
 * compare the argument types, the declaring class and the return type, in this
 * order, and the first non-equal pair of types determines the order.
 */
class MethodComparatorFull implements Comparator<Method> {
  /**
   * Comparator for arguments (i.e. ignores return types and declaring
   * classes)
   */
  private Comparator<Method> argOnlyComparator =
      ComparatorFactory.createMethodComparator();

  @Override
  public int compare(final Method m1, final Method m2) {
    // first try to decide order based on arguments
    int argCompare = argOnlyComparator.compare(m1, m2);

    if (argCompare != 0) {
      return argCompare;
    }

    // if arguments are the same, then try to pick based on the
    // declaring class
    Class<?> declType1 = m1.getDeclaringClass();
    Class<?> declType2 = m2.getDeclaringClass();

    if (declType1.equals(declType2)) {
      // if declaring class is the same, pick the most special return type
      Class<?> retType1 = m1.getReturnType();
      Class<?> retType2 = m2.getReturnType();

      if (retType1.equals(retType2)) {
        return 0;
      } else if (retType1.isAssignableFrom(retType2)) {
        return 1;
      } else {
        return -1;
      }
    } else if (declType1.isAssignableFrom(declType2)) {
      return 1;
    } else {
      return -1;
    }
  }
}
