// Copyright 2016-2018 Diffblue limited. All rights reserved.

package com.diffblue.deeptestutils.mock;

import java.lang.reflect.Method;
import java.util.LinkedList;

import org.powermock.reflect.exceptions.TooManyMethodsFoundException;
import org.powermock.reflect.internal.WhiteboxImpl;

/**
 * <code>MemberMatcher</code> is a utility class for finding members of classes.
 * <p>
 * We currently use it to find method members only. The functionality uses
 * PowerMock library and the following PowerMock's MemberMatcher bug is fixed:
 * - TG-5254
 *
 * @author <a href="http://diffblue.com">Diffblue</a>
 */
public final class MemberMatcher {

    /**
     * Private constructor to prevent instantiation of the utility class.
     */
    private MemberMatcher() {
    }

    /**
     * Get a method matching the given name and parameter types.
     * <p>
     * We first call the MemberMatcher of PowerMock to try and find a
     * matching method. We return it's result unless it finishes with the
     * TooManyMethodsFoundException exception. In that case, we catch the
     * exception and try to find the method ourselves as follows. First, we try
     * to find a matching method in the provided class. If no matching method
     * is found then we proceed to the super class continuing up the class
     * hierarchy. A matching method is a method with the provided name and
     * parameter types that exaclty match the provided ones or are less
     * specific. If there is more than one matching method found in a class
     * than the one with the most specific return type is chosen.
     *
     * @param declaringClass The class to search
     * @param methodName The name of the desired method
     * @param parameterTypes Types that can be fed as arguments to the method
     *                      (can be more specific than the method's declared
     *                       types)
     * @return A matching method if such exists
     * @throws MethodNotFoundException If a method cannot be found in the
     *                      hierarchy.
     */
    public static Method method(final Class<?> declaringClass,
                                final String methodName,
                                final Class<?>... parameterTypes) {

        try {
            return org.powermock.api.support.membermodification.MemberMatcher
                    .method(declaringClass, methodName, parameterTypes);
        } catch (TooManyMethodsFoundException e) {

            Class<?> currentClass = declaringClass;

            while (currentClass != null) {
                Method[] declaredMethods = currentClass.getDeclaredMethods();
                LinkedList<Method> matchingMethods = new LinkedList<Method>();

                for (Method method : declaredMethods) {
                    boolean parametersMatch =
                            WhiteboxImpl.checkIfParameterTypesAreSame(
                                    method.isVarArgs(), parameterTypes,
                                    method.getParameterTypes());
                    if (methodName.equals(method.getName())
                            && parametersMatch) {
                        matchingMethods.add(method);
                    }
                }

                switch (matchingMethods.size()) {
                    case 0:
                        currentClass = currentClass.getSuperclass();
                        break;
                    case 1:
                        return matchingMethods.get(0);
                    default:
                        Method candidate = matchingMethods.get(0);
                        for (int i = 1; i < matchingMethods.size(); i++) {
                            if (candidate.getReturnType().isAssignableFrom(
                                    matchingMethods.get(i).getReturnType())) {
                                candidate = matchingMethods.get(i);
                            }
                        }
                        return candidate;
                }
            }

            WhiteboxImpl.throwExceptionIfMethodWasNotFound(declaringClass,
                    methodName, null, new Object[]{parameterTypes});
            return null;
        }
    }
}
