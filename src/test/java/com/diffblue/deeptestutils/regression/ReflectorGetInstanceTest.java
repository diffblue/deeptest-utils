package com.diffblue.deeptestutils.regression;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import java.lang.reflect.InvocationTargetException;
import com.diffblue.deeptestutils.Reflector;
import org.junit.rules.ExpectedException;
import org.mockito.internal.matchers.Null;

public class ReflectorGetInstanceTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  // This test checks that in a class with a constructor with no arguments,
  // calling Reflector.getInstance does not cause a call to that constructor.
  // If the constructor was called, a NullPointerException would happen as
  // integerField has its default value of null.
  @Test
  public void concreteClassNoConstructorCall()
      throws InvocationTargetException {
    ClassWithConstructor cwc = (ClassWithConstructor) Reflector.getInstance(
        "com.diffblue.deeptestutils.regression.ClassWithConstructor");
    Assert.assertEquals(cwc.intField, 0);
  }

  private char privateInnerClassHelper(Object obj) {
    if (obj == null) {
      return 'n';
    }
    return 'i';
  }

  // This test checks that we can create instances of private inner classes with
  // Reflector.getInstance.
  @Test
  public void privateInnerClass() throws InvocationTargetException {
    Object obj = Reflector.getInstance(
        "com.diffblue.deeptestutils.regression.PrivateClassOuter$PrivateClass");
    char retval = privateInnerClassHelper(obj);
    Assert.assertEquals(retval, 'i');
  }

  // For a private class that implements an interface, we can instantiate it and
  // call the methods defined in the interface on the generated instance.
  @Test
  public void privateInnerClassWithImplementingMethod()
      throws InvocationTargetException {
    Interface in = (Interface) Reflector.getInstance(
        "com.diffblue.deeptestutils.regression"
            + ".PrivateClassOuter$PrivateImplementingClass");
    int retval = in.method();
    Assert.assertEquals(retval, 11);
  }

  // When we call Reflector.getInstance on an abstract class and try to call an
  // abstract method on it, an AbstractMethodError is thrown.
  @Test
  public void abstractClassAbstractMethod() throws InvocationTargetException {
    thrown.expect(AbstractMethodError.class);
    AbstractClass ac = (AbstractClass) Reflector.getInstance(
        "com.diffblue.deeptestutils.regression.AbstractClass");
    ac.abstractMethod();
  }

  // When we call Reflector.getInstance on an abstract class and call a concrete
  // method on it, no exceptions or errors are thrown and the class behaves as
  // if it was concrete.
  @Test
  public void abstractClassConcreteMethod() throws InvocationTargetException {
    AbstractClass ac = (AbstractClass) Reflector.getInstance(
        "com.diffblue.deeptestutils.regression.AbstractClass");
    int retval = ac.concreteMethod();
    Assert.assertEquals(retval, 23);
  }

  // When we call Reflector.getInstance on an abstract class and call concrete
  // methods on it that change its state, as long as no abstract methods are
  // also called it behaves just as a concrete class would.
  @Test
  public void abstractClassConcreteMutators() throws InvocationTargetException {
    AbstractClass ac = (AbstractClass) Reflector.getInstance(
        "com.diffblue.deeptestutils.regression.AbstractClass");
    ac.setField("");
    ac.appendToField("test");
    ac.appendToField("cover");
    Assert.assertEquals(ac.getField(), "testcover");
  }

  // Just like in the case of concrete classes, calling Reflector.getInstance on
  // an abstract class does not cause a call to the constructor of that class.
  // If the constructor was called, the value of the field would be 100.
  @Test
  public void abstractClassNoConstructorCall()
      throws InvocationTargetException {
    AbstractClassWithConstructor ac =
        (AbstractClassWithConstructor) Reflector.getInstance(
            "com.diffblue.deeptestutils.regression."
                + "AbstractClassWithConstructor");
    Assert.assertEquals(ac.getField(), 0);
  }

  private char unusedInterfaceHelper(Interface in) {
    if (in == null) {
      return 'n';
    }
    return 'i';
  }

  // We can create instances of interfaces using Reflector.getInstance. If we do
  // not call any methods on the instance, it should behave just like an
  // instance of an implementing class.
  @Test
  public void unusedInterface() throws InvocationTargetException {
    Interface in = (Interface) Reflector.getInstance(
        "com.diffblue.deeptestutils.regression.Interface");
    char retval = unusedInterfaceHelper(in);
    Assert.assertEquals(retval, 'i');
  }

  // When we call Reflector.getInstance on an interface and try to call a
  // (non-default) method on it, an AbstractMethodError is thrown.
  // TODO Add a test for default methods of interfaces, which should only be run
  // TODO using Java 8, as default methods were a new feature in Java 8.
  @Test
  public void interfaceMethod() throws InvocationTargetException {
    thrown.expect(AbstractMethodError.class);
    Interface in = (Interface) Reflector.getInstance(
        "com.diffblue.deeptestutils.regression.Interface");
    in.method();
  }

  // When we call Reflector.getInstance on a concrete class whose static
  // initialiser throws an exception, it wraps this exception within an
  // InvocationTargetException.
  @Test
  public void concreteClassWithBadStaticInit() throws Throwable {
    try {
      Reflector.getInstance(
          "com.diffblue.deeptestutils.regression.BadStaticInit");
      Assert.assertTrue(false);
    } catch (InvocationTargetException e) {
      Assert.assertEquals(e.getCause().getClass(), ExceptionInInitializerError.class);
      Assert.assertEquals(e.getCause().getCause().getClass(), NullPointerException.class);
    }
  }

  // When we call Reflector.getInstance on an abstract class whose static
  // initialiser throws an exception, it should wrap this exception within an
  // InvocationTargetException.
  // TODO Fix this (TG-5895) and enable this test.
  @Test
  @Ignore
  public void abstractClassWithBadStaticInit() throws Throwable {
    try {
      Reflector.getInstance(
          "com.diffblue.deeptestutils.regression.AbstractBadStaticInit");
    } catch (InvocationTargetException e) {
      Assert.assertEquals(e.getCause().getClass(), NullPointerException.class);
    }
  }
}
