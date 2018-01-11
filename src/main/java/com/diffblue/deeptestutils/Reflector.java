package com.diffblue.deeptestutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

import org.objenesis.ObjenesisStd;

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
   * Changes a given field of an object instance via reflection, bypassing the
   * private modifier.
   *
   * @param obj an <code>Object</code> instance to change
   * @param fieldName a <code>String</code> the name of the field to change
   * @param newVal an <code>Object</code> the new value for the field
   *
   * @exception NoSuchFieldException if a field with the specified name is not
   *     found.
   * @exception IllegalArgumentException if the specified object is not an
   *     instance of the class or interface declaring the underlying field (or a
   *     subclass or implementor thereof), or if an unwrapping conversion fails.
   * @exception IllegalAccessException if an error occurs
   */
  public static void setField(
      final Object obj,
      final String fieldName,
      final Object newVal)
      throws
      NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    setField(obj.getClass(), obj, fieldName, newVal);
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
  private static <T> void setField(
          final Class<T> c, final Object o, final String fieldName,
          final Object newVal) throws
          NoSuchFieldException, IllegalArgumentException,
          IllegalAccessException {

    if (c == null) {
      throw new NoSuchFieldException();
    }
    Optional<Field> field =
        Arrays.stream(c.getDeclaredFields())
        .filter(f -> f.getName().equals(fieldName)).findAny();
    if (!field.isPresent()) {
      setField(c.getSuperclass(), o, fieldName, newVal);
    } else {
      Field property = field.get();
      property.setAccessible(true);

      // remove final modifier
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField
        .setInt(property, property.getModifiers() & ~Modifier.FINAL);
      try {
        property.set(o, newVal);
      } catch (IllegalAccessException ex) {
        throw new RuntimeException(ex); // Should never happen.
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
   *
   * @exception NoSuchFieldException if a field with the specified name is not
   *     found.
   * @exception IllegalArgumentException if the specified object is not an
   *     instance of the class or interface declaring the underlying field (or a
   *     subclass or implementor thereof), or if an unwrapping conversion fails.
   * @exception IllegalAccessException if an error occurs
   */
  private static <T> Object getInstanceField(
      final Class<T> c,
      final Object o,
      final String fieldName)
      throws
      NoSuchFieldException,
      IllegalArgumentException,
      IllegalAccessException {
    if (c == null) {
      throw new NoSuchFieldException();
    }
    Optional<Field> field =
        Arrays.stream(c.getDeclaredFields())
        .filter(f -> f.getName().equals(fieldName)).findAny();
    if (!field.isPresent()) {
      return getInstanceField(c.getSuperclass(), o, fieldName);
    } else {
      Field property = field.get();
      property.setAccessible(true);

      try {
        return property.get(o);
      } catch (IllegalAccessException ex) {
        throw new RuntimeException(ex); // Should never happen.
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
   * @exception NoSuchFieldException if the field does not exist
   * @exception IllegalArgumentException if the specified object is not an
   *     instance of the class or interface declaring the underlying field (or a
   *     subclass or implementor thereof), or if an unwrapping conversion fails.
   * @exception IllegalAccessException if an error occurs
   */
  public static Object getInstanceField(
    final Object obj,
    final String fieldName)
      throws
      NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    return getInstanceField(obj.getClass(), obj, fieldName);
  }

  /**
   * <code>forName</code> returns class of given type name, including primitive
   * types.
   *
   * @param className name of class/type as <code>String</code>
   * @return the <code>Class</code> object
   * @exception ClassNotFoundException if class cannot be found
   */
  public static Class<?> forName(final String className)
      throws ClassNotFoundException {
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
      return Class.forName(arrayPrefix + cleanedName);
    }

    return Class.forName(className);
  }

  /**
   * Returns class of exception or Throwable.
   *
   * @param className name of class as <code>String</code>
   * @return the <code>Class</code> object
   * @exception ClassCastException if class cannot be cast to Throwable
   * @exception ClassNotFoundException if class cannot be found
   */
  @SuppressWarnings("unchecked")
  public static Class<? extends Throwable> toThrowableClass(
      final String className)
      throws ClassCastException, ClassNotFoundException {
    final Class throwableClass = Throwable.class;
    Class<?> cl = forName(className);
    if (cl.isAssignableFrom(throwableClass)) {
      return (Class<? extends Throwable>) cl;
    } else {
      throw
        new ClassCastException("cannot cast " + className + " to Throwable");
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
  private static HashMap<String, Class<?>> classMap = new HashMap<>();

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
   * @param c the class as to make public
   */
  private static void makePublic(final CtClass c) {
    int modifier = c.getModifiers();
    modifier = modifier & ~(javassist.Modifier.PRIVATE
                            | javassist.Modifier.PROTECTED);
    modifier = modifier | javassist.Modifier.PUBLIC;
    c.setModifiers(modifier);
  }

  /**
   * This forces the creation of an instance for a given class name. If the
   * class provides a public default constructor, it is called. If the class has
   * a private default constructor, it is made accessible and is then called.
   *
   * @param <T> type parameter of the class
   * @param cl a <code>Class</code> the class to instantiate
   * @return an <code>Object</code> which is an instance of the specified class
   */
  @SuppressWarnings("unchecked")
  public static <T> T getInstance(final Class<T> cl) {
    Optional<Constructor<?>> ctor = getDefaultConstructor(cl);
    if (ctor.isPresent()) {
      Constructor<?> defaultCtor = ctor.get();
      defaultCtor.setAccessible(true);
      try {
        return (T) defaultCtor.newInstance();
      } catch (InstantiationException
               | InvocationTargetException
               | IllegalAccessException ex) {
        return (T) new ObjenesisStd().newInstance(cl);
      }
    }
    return (T) new ObjenesisStd().newInstance(cl);
  }

  /**
   * Force the creation of an instance for a given class name.
   *
   * @param <T> type parameter of the return value
   * @param className a <code>String</code> giving the name of the class
   * @return an <code>Object</code> which is an instance of the specified class
   *
   * @throws ClassNotFoundException if the class cannot be found in the
   * classpath
   * @throws NotFoundException signals that something could not be found
   * @throws CannotCompileException if the class cannot be compiled
   * @throws InstantiationException if the class cannot be instantiated
   * @throws IllegalAccessException if the class cannot be accessed
   * @throws BadBytecode if the on-the-fly compilation uses an invalid bytecode
   */
  public static <T> Object getInstance(final String className)
      throws
      ClassNotFoundException,
      NotFoundException,
      CannotCompileException,
      InstantiationException,
      IllegalAccessException,
      BadBytecode {
    ClassPool pool = ClassPool.getDefault();
    CtClass cl = pool.get(className);

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

    // we consider a class abstract if any method has no body
    if (isAbstract(cl) || cl.isInterface()) {
      String packageName = "com.diffblue.test_gen.";
      String newClassName = packageName + removePackageFromName(className);

      CtClass implementation = pool.getOrNull(newClassName + "_implementation");
      if (implementation == null) {
        implementation = pool.makeClass(newClassName + "_implementation");

        if (cl.isInterface()) {
          implementation.setInterfaces(new CtClass[] {cl });
        } else {
          implementation.setSuperclass(cl);
        }

        // look for constructor
        // create default constructor if none exists
        boolean foundDefault = false;
        if (!cl.isInterface()) {
          for (CtConstructor ctor : cl.getConstructors()) {
            if (ctor.getParameterTypes().length == 0
                && (ctor.getModifiers() & javassist.Modifier.ABSTRACT) == 0
                && !ctor.isEmpty()) {
              foundDefault = true;
              break;
            }
          }
        }
        if (!foundDefault) {
          CtConstructor newCtor =
              new CtConstructor(new CtClass[] {}, implementation);
          newCtor.setBody("{}");
          implementation.addConstructor(newCtor);
        }

        // declared methods or only methods ?
        for (CtMethod m : cl.getDeclaredMethods()) {
          if (isAbstract(m)) {
            CtMethod method = CtNewMethod.make(javassist.Modifier.PUBLIC,
                                               m.getReturnType(),
                                               m.getName(),
                                               m.getParameterTypes(),
                                               m.getExceptionTypes(),
                                               null,
                                               implementation);
            implementation.addMethod(method);
          }
        }

        Class<?> ic = pool.toClass(implementation);

        classMap.put(newClassName + "_implementation", ic);
        return getInstance(ic);
      } else {
        return getInstance((Class<?>) classMap
          .get(newClassName + "_implementation"));
      }
    } else {
      return getInstance(Class.forName(className));
    }
  }

  /**
   * Checks whether the <code>ABSTRACT</code> flag of a method is set.
   *
   * @param m the method to check
   * @return a <code>boolean</code> value indicating whether the method is
   * abstract.
   */
  private static boolean isAbstract(final CtMethod m) {
    return ((m.getModifiers() & javassist.Modifier.ABSTRACT) != 0);
  }

  /**
   * Checks whether the <code>ABSTRACT</code> flag of a class is set.
   *
   * @param c the class to check
   * @return a <code>boolean</code> value indicating whether the class is
   * abstract.
   */
  private static boolean isAbstract(final CtClass c) {
    if ((c.getModifiers() & javassist.Modifier.ABSTRACT) != 0) {
      return true;
    }
    for (CtMethod m : c.getDeclaredMethods()) {
      if (isAbstract(m)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the default constructor if one exists.
   *
   * @param c the class to search the constructor in
   * @return an <code>Optional</code> value holding a <code>Constructor</code>
   *     object if it exists
   */
  private static Optional<Constructor<?>> getDefaultConstructor(
      final Class<?> c) {
    return Arrays.stream(c.getDeclaredConstructors())
      .filter(ctor -> ctor.getParameterCount() == 0).findAny();
  }
}
