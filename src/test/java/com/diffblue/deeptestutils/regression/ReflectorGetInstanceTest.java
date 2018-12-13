package com.diffblue.deeptestutils.regression;

import org.junit.Assert;
import org.junit.Test;
import java.lang.reflect.InvocationTargetException;
import com.diffblue.deeptestutils.Reflector;

public class ReflectorGetInstanceTest {

  // This test checks that in a class with a constructor with no arguments,
  // calling Reflector.getInstance does not cause a call to that constructor.
  // If the constructor was called, a NullPointerException would happen as
  // integerField has its default value of null.
  @Test
  public void checkNoConstructorCall() throws InvocationTargetException {
    ClassWithConstructor cwc = (ClassWithConstructor) Reflector.getInstance
        ("com.diffblue.deeptestutils.regression.ClassWithConstructor");
    Assert.assertEquals(cwc.intField, 0);
  }
}
