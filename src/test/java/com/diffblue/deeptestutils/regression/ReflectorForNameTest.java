package com.diffblue.deeptestutils.regression;

import com.diffblue.deeptestutils.Reflector;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectorForNameTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  // This test demonstrates how to test a private method of a public class,
  // getting an instance of the class using Reflector.getInstance and the method
  // from the Class object returned by Reflector.getName.
  @Test
  public void reflectAndCallPrivateMethod() throws InvocationTargetException,
      IllegalAccessException, NoSuchMethodException {
    ClassWithPrivateMethod obj = (ClassWithPrivateMethod) Reflector.getInstance(
        "com.diffblue.deeptestutils.regression.ClassWithPrivateMethod");
    Class<?> cl = Reflector.forName(
        "com.diffblue.deeptestutils.regression.ClassWithPrivateMethod");
    Method method = cl.getDeclaredMethod("privateMethod");
    method.setAccessible(true);
    // In Java 8, this line could be
    // int retval = (int) method.invoke(obj);
    int retval = (Integer) method.invoke(obj);
    Assert.assertEquals(12, retval);
  }

  // Similar to reflectAndCallPrivateMethod, but without the line that sets the
  // accessibility of the method. In this case, an exception is thrown.
  @Test
  public void callWithoutSettingAccessibility()
      throws InvocationTargetException, IllegalAccessException,
      NoSuchMethodException {
    thrown.expect(IllegalAccessException.class);
    ClassWithPrivateMethod obj = (ClassWithPrivateMethod) Reflector.getInstance(
        "com.diffblue.deeptestutils.regression.ClassWithPrivateMethod");
    Class<?> cl = Reflector.forName(
        "com.diffblue.deeptestutils.regression.ClassWithPrivateMethod");
    Method method = cl.getDeclaredMethod("privateMethod");
    method.invoke(obj);
  }
}
