package com.diffblue.cover.test;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 **/
public final class Assert {
  @Rule
  public static ExpectedException thrown = ExpectedException.none();

  /**
   *
   **/
  public static void expectException(Class<? extends Throwable> exception) {
    thrown.expect(exception);
  }

  /**
   *
   **/
  public static void assertEquals(final Object a, final Object b) {
    org.junit.Assert.assertEquals(a, b);
  }

  /**
   *
   **/
  public static void assertNotEquals(final Object a, final Object b) {
    org.junit.Assert.assertTrue(a != b);
  }

  /**
   *
   **/
  private Assert() {
  }
}
