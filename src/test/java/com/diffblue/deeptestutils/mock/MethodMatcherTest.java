package com.diffblue.deeptestutils.mock;

import org.junit.Assert;
import org.junit.rules.ExpectedException;
import org.junit.Test;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.lang.reflect.InvocationTargetException;

class Y extends com.diffblue.deeptestutils.mock.subpackage.X<Integer> {
  protected Integer foo() {
    return 42;
  }
}

interface I {
  public Number foo();
}

class Z extends Y implements I {
  public Integer foo() {
    return 47;
  }
}

class Parent {
  public String toString() {
    return val;
  }
  public String val;
  public Parent(String val) {
    this.val = val;
  }

  static Parent simple(Parent a) {
    return new Parent("0");
  }

  static Parent returnSubtype(Parent a) {
    return new Parent("2");
  }

  static Parent argSubtype(Parent a) {
    return new Parent("4");
  }

  static Parent argSupertypeReturnSubtype(Child a) {
    return new Parent("6");
  }

  static Parent mix1(Child a) {
    return new Parent("8");
  }
}

class Child extends Parent{
  public Child(String val) {
     super(val);
  }

  static Parent simple(Parent a) {
   return new Parent("1");
  }

  static Child returnSubtype(Parent a) {
    return new Child("3");
  }

  static Parent argSubtype(Child a) {
    return new Parent("5");
  }

  static Child argSupertypeReturnSubtype(Parent a) {
    return new Child("7");
  }

  static Parent mix1(Child a) {
    return new Child("9x");
  }

  static Child mix1(Parent a) {
    return new Child("9");
  }
}

public class MethodMatcherTest {

  @org.junit.Rule
  public ExpectedException thrown = ExpectedException.none();

  @org.junit.Test
  public void simpleTest() throws IllegalAccessException, InvocationTargetException {
    Method m1 = DTUMemberMatcher.method(Child.class, "simple", Parent.class);
    Method m2 = DTUMemberMatcher.methodUsingWholeSignature(Child.class,"simple", Parent.class);

    Child child = new Child("child");
    Parent parent = new Parent("parent");

    String s1 = m1.invoke(child, parent).toString();
    String s2 = m2.invoke(child, parent).toString();

    // verify that we find the same method using both DTUMemberMatcher methods
    Assert.assertEquals(s1, s2);
    // verify that we find the correct method according to Java language specification
    Assert.assertEquals(child.simple(parent).toString(), s1);
    // verify that the matched method returns what we expect it to return
    Assert.assertEquals("1", s1);
  }

  @org.junit.Test
  public void returnSubtypeTest() throws IllegalAccessException, InvocationTargetException {
    Method m1 = DTUMemberMatcher.method(Child.class, "returnSubtype", Parent.class);
    Method m2 = DTUMemberMatcher.methodUsingWholeSignature(Child.class, "returnSubtype", Parent.class);

    Child child = new Child("child");
    Parent parent = new Parent("parent");

    String s1 = m1.invoke(child, parent).toString();
    String s2 = m2.invoke(child, parent).toString();

    // verify that we find the same method using both DTUMemberMatcher methods
    Assert.assertEquals(s1, s2);
    // verify that we find the correct method according to Java language specification
    Assert.assertEquals(child.returnSubtype(parent).toString(), s1);
    // verify that the matched method returns what we expect it to return
    Assert.assertEquals("3", s1);
  }

  @org.junit.Test
  public void argSubtypeTest() throws IllegalAccessException, InvocationTargetException {
    Method m1 = DTUMemberMatcher.method(Child.class, "argSubtype", Child.class);
    Method m2 = DTUMemberMatcher.methodUsingWholeSignature(Child.class,"argSubtype", Child.class);
    Method m3 = DTUMemberMatcher.method(Child.class, "argSubtype", Parent.class);
    Method m4 = DTUMemberMatcher.methodUsingWholeSignature(Child.class,"argSubtype", Parent.class);
    Method m5 = DTUMemberMatcher.method(Parent.class, "argSubtype", Child.class);
    Method m6 = DTUMemberMatcher.methodUsingWholeSignature(Parent.class,"argSubtype", Child.class);

    Child child = new Child("child");
    Parent parent = new Parent("parent");

    String s1 = m1.invoke(child, child).toString();
    String s2 = m2.invoke(child, child).toString();

    // verify that we find the same method using both DTUMemberMatcher methods
    Assert.assertEquals(s1, s2);
    // verify that we find the correct method according to Java language specification
    Assert.assertEquals(child.argSubtype(child).toString(), s1);
    // verify that the matched method returns what we expect it to return
    Assert.assertEquals("5", s1);

    String s3 = m3.invoke(child, parent).toString();
    String s4 = m4.invoke(child, parent).toString();

    // verify that we find the same method using both DTUMemberMatcher methods
    Assert.assertEquals(s3, s4);
    // verify that we find the correct method according to Java language specification
    Assert.assertEquals(child.argSubtype(parent).toString(), s3);
    // verify that the matched method returns what we expect it to return
    Assert.assertEquals("4", s3);

    String s5 = m5.invoke(parent, child).toString();
    String s6 = m6.invoke(parent, child).toString();

    // verify that we find the same method using both DTUMemberMatcher methods
    Assert.assertEquals(s5, s6);
    // verify that we find the correct method according to Java language specification
    Assert.assertEquals(parent.argSubtype(child).toString(), s5);
    // verify that the matched method returns what we expect it to return
    Assert.assertEquals("4", s5);
  }

  @org.junit.Test
  public void argSupertypeReturnSubtypeTest() throws IllegalAccessException, InvocationTargetException {
    String name = "argSupertypeReturnSubtype";
    Method m1 = DTUMemberMatcher.method(Child.class, name, Child.class);
    Method m2 = DTUMemberMatcher.methodUsingWholeSignature(Child.class, name, Child.class);
    Method m3 = DTUMemberMatcher.method(Child.class, name, Parent.class);
    Method m4 = DTUMemberMatcher.methodUsingWholeSignature(Child.class, name, Parent.class);

    Child child = new Child("child");
    Parent parent = new Parent("parent");

    String s1 = m1.invoke(child, child).toString();
    String s2 = m2.invoke(child, child).toString();

    // verify that we find the same method using both DTUMemberMatcher methods
    Assert.assertEquals(s1, s2);
    // verify that we find the correct method according to Java language specification
    Assert.assertEquals(child.argSupertypeReturnSubtype(child).toString(), s1);
    // verify that the matched method returns what we expect it to return
    Assert.assertEquals("6", s1);

    String s3 = m3.invoke(child, parent).toString();
    String s4 = m4.invoke(child, parent).toString();

    // verify that we find the same method using both DTUMemberMatcher methods
    Assert.assertEquals(s3, s4);
    // verify that we find the correct method according to Java language specification
    Assert.assertEquals(child.argSupertypeReturnSubtype(parent).toString(), s3);
    // verify that the matched method returns what we expect it to return
    Assert.assertEquals("7", s3);
  }

  @org.junit.Test
  public void mix1Test() throws IllegalAccessException, InvocationTargetException {
    String name = "mix1";
    Method m1 = DTUMemberMatcher.method(Child.class, name, Child.class);
    Method m2 = DTUMemberMatcher.methodUsingWholeSignature(Child.class, name, Child.class);
    Method m3 = DTUMemberMatcher.method(Child.class, name, Parent.class);
    Method m4 = DTUMemberMatcher.methodUsingWholeSignature(Child.class, name, Parent.class);

    Child child = new Child("child");
    Parent parent = new Parent("parent");

    String s1 = m1.invoke(child, child).toString();
    String s2 = m2.invoke(child, child).toString();

    // verify that we find the same method using both DTUMemberMatcher methods
    Assert.assertEquals(s1, s2);
    // verify that we find the correct method according to Java language specification
    Assert.assertEquals(child.mix1(child).toString(), s1);
    // verify that the matched method returns what we expect it to return
    Assert.assertEquals("9x", s1);

    String s3 = m3.invoke(child, parent).toString();
    String s4 = m4.invoke(child, parent).toString();

    // verify that we find the same method using both DTUMemberMatcher methods
    Assert.assertEquals(s3, s4);
    // verify that we find the correct method according to Java language specification
    Assert.assertEquals(child.mix1(parent).toString(), s3);
    // verify that the matched method returns what we expect it to return
    Assert.assertEquals("9", s3);
  }

  @org.junit.Test
  public void genericTest() throws IllegalAccessException, InvocationTargetException {
    String name = "foo";
    Method m1 = DTUMemberMatcher.method(Y.class, name);
    Method m2 = DTUMemberMatcher.methodUsingWholeSignature(Y.class, name);

    Y y = new Y();

    Object o1 = m1.invoke(y);
    Object o2 = m2.invoke(y);

    // verify that we find the same method using both DTUMemberMatcher methods
    Assert.assertEquals(o1, o2);
    // verify that we find the correct method according to Java language specification
    Assert.assertEquals(y.foo(), o1);
    // verify that the matched method returns what we expect it to return
    Assert.assertEquals(42, o1);
  }

  @org.junit.Test
  public void generic2Test() throws IllegalAccessException, InvocationTargetException {
    String name = "foo";
    Method m1 = DTUMemberMatcher.method(Z.class, name);
    Method m2 = DTUMemberMatcher.methodUsingWholeSignature(Z.class, name);

    Z z = new Z();

    Object o1 = m1.invoke(z);
    Object o2 = m2.invoke(z);

    // verify that we find the same method using both DTUMemberMatcher methods
    Assert.assertEquals(o1, o2);
    // verify that we find the correct method according to Java language specification
    Assert.assertEquals(z.foo(), o1);
    // verify that the matched method returns what we expect it to return
    Assert.assertEquals(47, o1);
  }
}
