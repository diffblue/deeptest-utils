package com.diffblue.cover.test;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;

import java.lang.reflect.Method;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.mockito.stubbing.OngoingStubbing;

/**
 *
 **/
public final class Mock {
  /**
   *
   **/
  public static void mockSuperClass(final String typeName) {
    PowerMockito.suppress(
      PowerMockito.constructorsDeclaredIn(Reflector.forName(typeName)));
  }

  /**
   *
   **/
  public void mockStatic(final String typeName, final boolean isAccessible, final boolean isFinal) {
      classType = Reflector.forName(typeName);
      classIsAccessible = isAccessible;
      classIsFinal = isFinal;
      currentMethodIsStatic = true;
      PowerMockito.mockStatic(classType);
  }

  /**
   *
   **/
  public <T> T mockInstance(final String typeName, final boolean isAccessible, final boolean isFinal) {
    classType = Reflector.forName(typeName);
      classIsAccessible = isAccessible;
      classIsFinal = isFinal;
      currentMethodIsStatic = false;
    return PowerMockito.mock((Class<T>)classType);
  }

  /**
   *
   **/
  public void when(
      final String methodName,
      final boolean isAccessible,
      final boolean isVoid) throws Exception {
    assert (currentMethodIsStatic);
    currentMethodName = methodName;
    currentMethodIsAccessible = isAccessible;
    currentMethodIsVoid = isVoid;
  }

  /**
   *
   **/
  public <T> void when(
      final String methodName,
      final boolean isAccessible,
      final boolean isVoid,
      final Class<?>... parameterTypes) throws Exception {
    assert (currentMethodIsStatic);
    currentMethodName = methodName;
    currentMethodIsAccessible = isAccessible;
    currentMethodIsVoid = isVoid;
    currentMethodParameterTypes = parameterTypes;
  }

  /**
   *
   **/
  public <T> void when(
      final T instance,
      final String methodName,
      final boolean isAccessible,
      final boolean isVoid) throws Exception {
    assert (!currentMethodIsStatic);
    currentMethodIsAccessible = isAccessible;
    currentMethodIsVoid = isVoid;
    currentInstance = instance;
    if (!isVoid && !classIsFinal) {
      ongoingStubbing = PowerMockito.when(instance, methodName);
    }
  }

  /**
   *
   **/
  public <T> void when(
      final T instance,
      final String methodName,
      final boolean isAccessible,
      final boolean isVoid,
      final Class<?>... parameterTypes) throws Exception {
    assert (!currentMethodIsStatic);
    currentMethodIsAccessible = isAccessible;
    currentMethodIsVoid = isVoid;
    currentInstance = instance;
    currentMethodParameterTypes = parameterTypes;
    if (!isVoid && !classIsFinal) {
      ongoingStubbing = PowerMockito.when(
        instance,
        methodName,
        DTUMemberMatcher.method(classType, methodName, parameterTypes));
    }
  }

  /**
   *
   **/
  public <T> void thenReturn(T value) throws Exception {
      if (ongoingStubbing instanceof OngoingStubbing) {
        ((OngoingStubbing<T>)ongoingStubbing).thenReturn(value);
      } else {
	if (currentMethodIsStatic) {
	  PowerMockito.doReturn(value).when(classType);
	  Method m = classType.getDeclaredMethod(currentMethodName);
	  m.setAccessible(true);
          m.invoke(null);
	}
      } 
  }

  /**
   *
   **/
  public <T> void thenThrow(Class<? extends Throwable> type) {
      ((OngoingStubbing<T>)ongoingStubbing).thenThrow(type);
  }

  /**
   *
   **/
  public void done() {
      ongoingStubbing = null;
  }

  /**
   *
   **/
  private Object ongoingStubbing;

  /**
   *
   **/
  private boolean currentMethodIsAccessible;

  /**
   *
   **/
  private boolean currentMethodIsVoid;

  /**
   *
   **/
  private boolean currentMethodIsStatic;

  /**
   *
   **/
  private String currentMethodName;

  /**
   *
   **/
  private Class<?>[] currentMethodParameterTypes;

  /**
   *
   **/
  private Object currentInstance;

  /**
   *
   **/
  private Class<?> classType;

  /**
   *
   **/
  private boolean classIsAccessible;

  /**
   *
   **/
  private boolean classIsFinal;
}
