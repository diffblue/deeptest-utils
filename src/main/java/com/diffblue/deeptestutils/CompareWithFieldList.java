package com.diffblue.deeptestutils;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

// Copyright 2016-2018 Diffblue limited. All rights reserved.

/**
 * <code>CompareWithFieldList</code> does a deep compare on objects and their
 * members.
 *
 * @author <a href="http://diffblue.com">Diffblue</a>
 */
public final class CompareWithFieldList {

  /**
   * Private constructor to prevent instantiation of the class with static
   * methods.
   */
  private CompareWithFieldList() {
  }

  /**
   * Output stream for debugging.
   */
  private static PrintStream debugOut;

  static {
    String outPath = System.getenv("CWFL_OUTPUT_FILE");
    if (outPath != null) {
      try {
        debugOut = new PrintStream(outPath);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    } else {
      debugOut = null;
    }
  }

  /**
   * Set of primitive classes.
   */
  private static HashSet<Class> primitives;

  static {
    primitives = new HashSet<Class>();
    primitives.add(Boolean.class);
    primitives.add(Character.class);
    primitives.add(Byte.class);
    primitives.add(Short.class);
    primitives.add(Integer.class);
    primitives.add(Long.class);
    primitives.add(Float.class);
    primitives.add(Double.class);
  }

  /**
   * Returns a named field of a class.
   *
   * @param cls the <code>Class</code> to look at
   * @param name the name of the field as <code>String</code> value
   * @return the <code>Field</code> value
   */
  private static Field getField(final Class cls, final String name) {
    Class currentClass = cls;
    while (cls != null) {
      try {
        return currentClass.getDeclaredField(name);
      } catch (NoSuchFieldException e) {
        currentClass = currentClass.getSuperclass();
      }
    }
    return null;
  }

  /**
   * Throws an exception with detailed error message.
   *
   * @param actual the original <code>Object</code>
   * @param expected the <code>Object</code> compared to that differs
   * @param prefix the difference prefix to get the error message as
   *   <code>String</code>
   */
  private static void fail(
      final Object actual,
      final Object expected,
      final String prefix) {
    String actualStr;
    if (actual == null) {
      actualStr = "null";
    } else {
      actualStr = (actual.getClass().getName() + " " + actual.toString());
    }
    String expectedStr;
    if (expected == null) {
      expectedStr = "null";
    } else {
      expectedStr = (expected.getClass().getName() + " " + expected.toString());
    }
    String fieldStr;
    if (prefix.equals("")) {
      fieldStr = "";
    } else {
      fieldStr = ("Field " + prefix + ": ");
    }
    throw new UnexpectedValueException(
      fieldStr + "Expected " + expectedStr + " got " + actualStr);
  }

  /**
   * Compares an object to either another object or a field
   * list. Returns on success, throws on error.
   *
   * @param primitiveOrFieldList the <code>Object</code> to compare to
   * @param real the <code>Object</code> to compare
   * @param prefix the collected structure description as <code>String</code>
   * @param objectStack mapping from arrays or field-lists to real pointers
   *     currently being checked by parents of this call; used in checking
   *     cyclic data structures
   */
  public static void compare(
      final Object primitiveOrFieldList,
      final Object real,
      final String prefix,
      final HashMap<Object, Object> objectStack) {

    if (real == null) {
      if (primitiveOrFieldList == null) {
        if (debugOut != null) {
          debugOut.printf("%s null as expected\n", prefix);
        }
        return;
      }
      fail(real, primitiveOrFieldList, prefix);
    }
    if (real.equals(primitiveOrFieldList)) {
      if (debugOut != null) {
        debugOut.printf("%s = %s as expected\n", prefix, real.toString());
      }
      return;
    }
    if (primitiveOrFieldList == null) {
      fail(real, primitiveOrFieldList, prefix);
    }
    if (primitives.contains(primitiveOrFieldList.getClass())) {
      fail(real, primitiveOrFieldList, prefix);
    }

    Object previousReal = objectStack.get(primitiveOrFieldList);
    if (previousReal != null) {
      if (previousReal != real) {
        throw new UnexpectedValueException(
          "At field %s, cyclic data structure has incorrect shape");
      } else {
        if (debugOut != null) {
          debugOut.printf(
              "%s is a cyclic back-pointer of expected shape\n", prefix);
        }
        return;
      }
    }

    // Note field-list-to-object-or-array correspondence for later
    // cyclic structure checks:
    objectStack.put(primitiveOrFieldList, real);

    if (primitiveOrFieldList.getClass().isArray()) {
      if (!real.getClass().isArray()) {
        fail(real, primitiveOrFieldList, prefix);
      }
      Object[] lhsArray = (Object[]) primitiveOrFieldList;
      Object[] rhsArray = (Object[]) real;
      if (lhsArray.length != rhsArray.length) {
        fail(real, primitiveOrFieldList, prefix);
      }
      for (int i = 0; i < lhsArray.length; ++i) {
        String newPrefix = prefix + "[" + i + "]";
        compare(lhsArray[i], rhsArray[i], newPrefix, objectStack);
      }
      objectStack.remove(primitiveOrFieldList);
      return;
    }
    if (!(primitiveOrFieldList instanceof FieldList)) {
      throw new RuntimeException(
        "Right-hand operand must be a primitive or a FieldList object");
    }

    FieldList rhs = (FieldList) primitiveOrFieldList;
    for (FieldValue v : rhs.getFields()) {
      Field field = getField(real.getClass(), v.getName());
      if (field == null) {
        throw new RuntimeException(
          "Real object of class " + real.getClass().getName()
          + " did not have expected field " + v.getName());
      }
      field.setAccessible(true);
      Object realval;
      try {
        realval = field.get(real);
      } catch (IllegalAccessException e) {
        // Should be impossible.
        throw new RuntimeException(e);
      }
      String newPrefix = prefix + "." + v.getName();
      compare(v.getValue(), realval, newPrefix, objectStack);
    }
    objectStack.remove(primitiveOrFieldList);
  }

  /**
   * Compares an object to either another object or a field
   * list. Returns on success, throws on error.
   *
   * @param primitiveOrFieldList the <code>Object</code> to compare to
   * @param real the <code>Object</code> to compare
   */
  public static void compare(
      final Object primitiveOrFieldList,
      final Object real) {
    HashMap<Object, Object> objectStack =
        new HashMap<Object, Object>();
    compare(primitiveOrFieldList, real, "", objectStack);
  }
}
