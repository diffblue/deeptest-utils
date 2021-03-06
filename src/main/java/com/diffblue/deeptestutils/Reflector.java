package com.diffblue.deeptestutils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.objenesis.ObjenesisStd;

// Copyright 2016-2018 Diffblue limited. All rights reserved.

/**
 * The <code>Reflector</code> class instantiates any Java object and sets any
 * field.
 *
 * @author <a href="http://diffblue.com">Diffblue</a>
 */
public final class Reflector {

  /**
   * private <code>Reflector</code> constructor.
   *
   */
  private Reflector() { }

  /**
   * Sets a given field of an object instance via reflection, bypassing the
   * private modifier.
   *
   * @param obj an <code>Object</code> instance to change
   * @param fieldName a <code>String</code> the name of the field to change
   * @param newVal an <code>Object</code> the new value for the field
   */
  public static void setField(final Object obj, final String fieldName,
                              final Object newVal) {
    try {
      setField(obj.getClass(), obj, fieldName, newVal);
    } catch (IllegalArgumentException e) {
      throw new DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
    }
  }

  /**
   * Sets a given static field of a class via reflection, bypassing the
   * private modifier.
   *
   * @param <T> type parameter of the class
   * @param c the <code>Class</code> of the static field to set
   * @param fieldName a <code>String</code> the name of the field to change
   * @param newVal an <code>Object</code> the new value for the field
   */
  public static <T> void setStaticField(final Class<T> c,
                                        final String fieldName,
                                        final Object newVal) {
    try {
      setField(c, null, fieldName, newVal);
    } catch (IllegalArgumentException e) {
      throw new DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
    }
  }

  /**
   * Sets a field of an object instance.
   *
   * @param <T> type parameter of the class
   * @param c the <code>Class</code> of the object to set
   * @param o the <code>Object</code> whose field is set
   * @param fieldName a <code>String</code> as the name of the field
   * @param newVal an <code>Object</code> holding the new value for the field
   *
   * @exception NoSuchFieldException if a field with the specified name is not
   *     found.
   * @exception IllegalArgumentException if the specified object is not an
   *     instance of the class or interface declaring the underlying field (or a
   *     subclass or implementor thereof), or if an unwrapping conversion fails.
   * @exception IllegalAccessException if an error occurs
   */
  public static <T> void setField(final Class<T> c, final Object o,
                                  final String fieldName, final Object newVal) {

    if (c == null) {
      throw new DeeptestUtilsRuntimeException(
          "Class of the field to be set cannot be null.",
          (new NoSuchFieldException()).getCause());
    }
    Field field = null;
    for (Field f : c.getDeclaredFields()) {
      if (f.getName().equals(fieldName)) {
        field = f;
        break;
      }
    }
    if (field == null) {
      setField(c.getSuperclass(), o, fieldName, newVal);
    } else {
      Field property = field;
      property.setAccessible(true);

      // remove final modifier
      Field modifiersField;
      try {
        modifiersField = Field.class.getDeclaredField("modifiers");
      } catch (NoSuchFieldException e) {
        throw new DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
      }
      modifiersField.setAccessible(true);
      try {
        modifiersField.setInt(property,
                              property.getModifiers() & ~Modifier.FINAL);
      } catch (IllegalAccessException e) {
        throw new DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
      }
      try {
        property.set(o, newVal);
      } catch (IllegalAccessException ex) {
        // Should never happen.
        throw new DeeptestUtilsRuntimeException(ex.getMessage(), ex.getCause());
      }
    }
  }

  /**
   * Get the value of a field from an object instance.
   *
   * @param <T> type parameter of the class
   * @param c the <code>Class</code> of the object to read
   * @param o the <code>Object</code> whose field is read
   * @param fieldName a <code>String</code> as the name of the field
   * @return the value the field holds
   */
  public static <T> Object getInstanceField(
      final Class<T> c,
      final Object o,
      final String fieldName) {
    if (c == null) {
      throw new DeeptestUtilsRuntimeException(fieldName
          + " is not a field in class " + c, new NoSuchFieldException());
    }
      Field field = null;
      for (Field f : c.getDeclaredFields()) {
        if (f.getName().equals(fieldName)) {
          field = f;
          break;
        }
      }
      if (field == null) {
        return getInstanceField(c.getSuperclass(), o, fieldName);
      } else {
        Field property = field;
        property.setAccessible(true);

        try {
          return property.get(o);
        } catch (IllegalArgumentException e) {
          throw new DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
        } catch (IllegalAccessException e) { // Should never happen.
          throw new DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
        }
      }
  }

  /**
   * Reads the content of a file of an object instance, bypassing the access
   * modifier.
   *
   * @param obj the <code>Object</code> instance to use
   * @param fieldName <code>String</code> value of the name of the field to read
   * @return the <code>Object</code> value read from the field
   */
  public static Object getInstanceField(
    final Object obj,
    final String fieldName) {
    return getInstanceField(obj.getClass(), obj, fieldName);
  }

  /**
   * <code>forName</code> returns class of given type name, including primitive
   * types.
   *
   * @param className name of class/type as <code>String</code>
   * @return the <code>Class</code> object
   */
  public static Class<?> forName(final String className) {
    if (className.equals("float")) {
      return float.class;
    }
    if (className.equals("byte")) {
      return byte.class;
    }
    if (className.equals("char")) {
      return char.class;
    }
    if (className.equals("short")) {
      return short.class;
    }
    if (className.equals("double")) {
      return double.class;
    }
    if (className.equals("int")) {
      return int.class;
    }
    if (className.equals("long")) {
      return long.class;
    }
    if (className.equals("boolean")) {
      return boolean.class;
    }
    // remove whitespace from string
    String cleanedName = className.replaceAll("\\s+", "");
    // check if array type
    if (cleanedName.endsWith("[]")) {
      String arrayPrefix = "";
      // collect information about multi-array
      while (cleanedName.endsWith("[]")) {
        int parenthesisIndex = cleanedName.length() - 2;
        arrayPrefix += ("[");
        cleanedName = cleanedName.substring(0, parenthesisIndex);
      }
      //primitive types need to be called in a special way
      if (cleanedName.equals("float")) {
        cleanedName = "F";
      } else if (cleanedName.equals("byte")) {
        cleanedName = "B";
      } else if (cleanedName.equals("char")) {
        cleanedName = "C";
      } else if (cleanedName.equals("short")) {
        cleanedName = "S";
      } else if (cleanedName.equals("double")) {
        cleanedName = "D";
      } else if (cleanedName.equals("int")) {
        cleanedName = "I";
      } else if (cleanedName.equals("long")) {
        cleanedName = "J";
      } else if (cleanedName.equals("boolean")) {
        cleanedName = "Z";
      } else {
        //non-primitive array types look like "[Lpackage.of.MyClass;"
        cleanedName = "L" + cleanedName + ";";
      }
      try {
        return Class.forName(arrayPrefix + cleanedName);
      } catch (ClassNotFoundException e) {
        throw new DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
      }

    }
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
    }
  }

  /**
   * Returns the Class of an Object if it extends or implements Throwable.
   *
   * @param className name of class as <code>String</code>
   * @return the <code>Class</code> object
   */
  @SuppressWarnings("unchecked")
  public static Class<? extends Throwable> toThrowableClass(
      final String className) {
    final Class throwableClass = Throwable.class;
    Class<?> cl = forName(className);
    if (throwableClass.isAssignableFrom(cl)) {
      return (Class<? extends Throwable>) cl;
    } else {
      throw
        new DeeptestUtilsRuntimeException("Cannot cast " + className
            + " to Throwable", new ClassCastException());
    }
  }

  /**
   * Removes the package from the name of a class, returning the class name
   * only.
   *
   * @param className name of the class as <code>String</code>
   * @return <code>String</code> value of the class name without preceeding
     package name
   */
  public static String removePackageFromName(final String className) {
    int lastSeparator = className.lastIndexOf('.');
    if (lastSeparator != -1) {
      return className.substring(lastSeparator + 1);
    } else {
      return className;
    }
  }

  /**
   * <code>classMap</code> keeps a cache of created classes.
   *
   */
  private static HashMap<String, Class<?>> classMap =
    new HashMap<String, Class<?>>();

  /**
   * <code>makePublic</code> sets member flag to public.
   *
   * @param m the member to make public
   */
  private static void makePublic(final CtMember m) {
    int modifier = m.getModifiers();
    modifier = modifier & ~(javassist.Modifier.PRIVATE
                            | javassist.Modifier.PROTECTED);
    modifier = modifier | javassist.Modifier.PUBLIC;
    m.setModifiers(modifier);
  }

  /**
   * Sets class flag to public.
   *
   * @param c the class to make public
   */
  private static void makePublic(final CtClass c) {
    int modifier = c.getModifiers();
    modifier = modifier & ~(javassist.Modifier.PRIVATE
                            | javassist.Modifier.PROTECTED);
    modifier = modifier | javassist.Modifier.PUBLIC;
    c.setModifiers(modifier);
  }

  /**
   * Sets the flag of the given class and all of its fields, methods and
   * constructors to public.
   * We use this on abstract classes and interfaces so that our newly
   * constructed implementing classes in Reflector.getInstance can inherit from
   * them.
   *
   * @param cl the class to make public
   */
  private static void makeFullyPublic(final CtClass cl) {
    for (CtMethod m : cl.getDeclaredMethods()) {
      makePublic(m);
    }
    for (CtConstructor ctor : cl.getDeclaredConstructors()) {
      makePublic(ctor);
    }
    for (CtField f : cl.getDeclaredFields()) {
      makePublic(f);
    }
    makePublic(cl);
  }

  /**
   * This forces the creation of an instance for a given class using Objenesis.
   *
   * @param <T> type parameter of the class
   * @param cl a <code>Class</code> the class to instantiate
   * @return an <code>Object</code> which is an instance of the specified class
   *
   * @throws InvocationTargetException if an `ExceptionInInitializerError` was
   *   thrown by ObjenesisStd.newInstance.
   *   See {@link #getInstance(String) getInstance}.
   */
  @SuppressWarnings("unchecked")
  public static <T> T getInstance(final Class<T> cl)
      throws InvocationTargetException {
    try {
      return (T) new ObjenesisStd().newInstance(cl);
    } catch (ExceptionInInitializerError ex) {
      throw new InvocationTargetException(ex.getCause());
    }
  }

  /**
   * Force the creation of an instance for a given class name.
   *
   * @param <T> type parameter of the return value
   * @param className a <code>String</code> giving the name of the class
   * @return an <code>Object</code> which is an instance of the specified class
   *
   * @throws InvocationTargetException if an `ExceptionInInitializerError` was
   *   thrown, which signals an exception in the static initializer of the
   *   class. Such an error could be thrown by Class.forName or
   *   Objenesis.newInstance, both of which call the static initializer (for
   *   the latter, see {@link #getInstance(Class) getInstance}.
   *   We extract the cause of the error and wrap it within an
   *   `InvocationTargetException`.
   */
  public static <T> Object getInstance(final String className)
      throws InvocationTargetException {
    ClassPool pool = ClassPool.getDefault();
    CtClass cl;
    try {
      cl = pool.get(className);
    } catch (NotFoundException e) {
      throw new DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
    }
    if (isAbstract(cl) || cl.isInterface()) {
      makeFullyPublic(cl);
      String implementingClassName = "com.diffblue.cover"
          + removePackageFromName(className) + "Impl";
      CtClass implementingCtClass = pool.getOrNull(implementingClassName);
      if (implementingCtClass != null) {
        return getInstance((Class<?>) classMap.get(implementingClassName));
      }
      implementingCtClass = pool.makeClass(implementingClassName);
      if (cl.isInterface()) {
        implementingCtClass.setInterfaces(new CtClass[] {cl });
      } else {
        try {
          implementingCtClass.setSuperclass(cl);
        } catch (CannotCompileException e) {
          throw new
              DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
        }
      }
      Class<?> implementingClass;
      try {
        implementingClass = pool.toClass(implementingCtClass);
      } catch (CannotCompileException e) {
        throw new
            DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
      }
      classMap.put(implementingClassName, implementingClass);
      return getInstance(implementingClass);
    } else {
      try {
        return getInstance(Class.forName(className));
      } catch (ExceptionInInitializerError ex) {
        throw new InvocationTargetException(ex.getCause());
      } catch (ClassNotFoundException e) {
        throw new DeeptestUtilsRuntimeException(e.getMessage(), e.getCause());
      }
    }
  }

  /**
   * Checks whether the <code>ABSTRACT</code> flag of a class is set.
   *
   * @param c the class to check
   * @return a <code>boolean</code> value indicating whether the class is
   * abstract.
   */
  private static boolean isAbstract(final CtClass c) {
    return (c.getModifiers() & javassist.Modifier.ABSTRACT) != 0;
  }
}
