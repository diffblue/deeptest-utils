// Copyright 2017-2018 Diffblue Limited. All Rights Reserved.

package com.diffblue.deeptestutils.powermock;

import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.reflect.exceptions.TooManyMethodsFoundException;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <code>DTUWhiteboxImpl</code> extends the WhiteboxImpl class in PowerMock
 * and fixes the issues below.
 * - TG-5254
 *
 * @author <a href="http://diffblue.com">Diffblue</a>
 */
public class DTUWhiteboxImpl extends WhiteboxImpl {

    /**
     * Check if the methods in the list differ only in return type.
     *
     * @param methodsList list of methods to inspect
     * @return true if all methods have the same parameter types, false
     *      otherwise or if the list is empty.
     */
    public static boolean allMethodsHaveTheSameParameters(
            final List<Method> methodsList) {
        if (methodsList.isEmpty()) {
            return false;
        }

        Method first = methodsList.get(0);
        boolean val = true;
        for (int i = 1; i < methodsList.size(); i++) {
            val &= checkIfParameterTypesAreSame(methodsList.get(i).isVarArgs(),
                    first.getParameterTypes(),
                    methodsList.get(i).getParameterTypes());
        }
        return val;
    }

    /**
     * If there is more than one method in the list then do the following:
     *  - If there is a method declared in the provided class type then remove
     *    all methods declared elsewhere. If there is more than one method
     *    remaining then choose the one with the most specific return type.
     *  - In all other cases do nothing, i.e., return the original list.
     *
     * @param <T> the generic type
     * @param type the class type
     * @param methodsList list of methods to inspect
     * @return list of methods declared in the provided class type with
     *      removed duplicates, the original list otherwise
     */
    public static <T> List<Method> removeDuplicatesBasedOnReturnType(
            final Class<T> type, final List<Method> methodsList) {
        if (methodsList.size() < 2) {
            return methodsList;
        }

        List<Method> methodsListTemp = new LinkedList<Method>();
        for (Method method : methodsList) {
            if (method.getDeclaringClass() == type) {
                methodsListTemp.add(method);
            }
        }
        switch (methodsListTemp.size()) {
            case 0:
                return methodsList;
            case 1:
                return methodsListTemp;
            default:
                Method candidate = methodsListTemp.get(0);
                for (int i = 1; i < methodsListTemp.size(); i++) {
                    if (candidate.getReturnType().isAssignableFrom(
                            methodsListTemp.get(i).getReturnType())) {
                        candidate = methodsListTemp.get(i);
                    }
                }
                return Collections.singletonList(candidate);
        }
    }

    /**
     * Copied from org.powermock.reflect.internal.WhiteboxImpl
     *
     * Throw exception when multiple method matches found.
     *
     * @param helpInfo the help info
     * @param methods  the methods
     */
    static void throwExceptionWhenMultipleMethodMatchesFound(
            final String helpInfo,
            final Method[] methods) {
        if (methods == null || methods.length < 2) {
            throw new IllegalArgumentException("Internal error: "
                    + "throwExceptionWhenMultipleMethodMatchesFound "
                    + "needs at least two methods.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Several matching methods found, please specify the ");
        sb.append(helpInfo);
        sb.append(" so that PowerMock can determine which method you're"
                + " referring to.\n");
        sb.append("Matching methods in class ").append(methods[0]
                .getDeclaringClass().getName()).append(" were:\n");

        for (Method method : methods) {
            sb.append(method.getReturnType().getName()).append(" ");
            sb.append(method.getName()).append("( ");
            final Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> paramType : parameterTypes) {
                sb.append(paramType.getName()).append(".class ");
            }
            sb.append(")\n");
        }
        throw new TooManyMethodsFoundException(sb.toString());
    }

    /**
     * Copied from org.powermock.reflect.internal.WhiteboxImpl and added a
     * check to remove duplicates based on return types (e.g., if both the
     * provided class and it's parent have a matching method and the child's
     * method return type extends the parent's method return type then the
     * parent's method should be recognized as a duplicate and removed).
     *
     * Finds and returns a method based on the input parameters. If no
     * <code>parameterTypes</code> are present the method will return the first
     * method with name <code>methodNameToMock</code>. If no method was found,
     * <code>null</code> will be returned. If no <code>methodName</code> is
     * specified the method will be found based on the parameter types. If
     * neither method name nor parameters are specified an
     *
     * @param <T>            the generic type
     * @param type           the type
     * @param methodName     the method name
     * @param parameterTypes the parameter types
     * @return the method {@link IllegalArgumentException} will be thrown.
     */
    public static <T> Method findMethod(final Class<T> type,
                                        final String methodName,
                                        final Class<?>... parameterTypes) {
        if (methodName == null && parameterTypes == null) {
            throw new IllegalArgumentException(
                    "You must specify a method name or parameter types.");
        }
        List<Method> matchingMethodsList = new LinkedList<Method>();
        for (Method method : getAllMethods(type)) {
            if (methodName == null || method.getName().equals(methodName)) {
                if (parameterTypes != null && parameterTypes.length > 0) {
                    // If argument types was supplied, make sure that they
                    // match.
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (!checkIfParameterTypesAreSame(method.isVarArgs(),
                            parameterTypes, paramTypes)) {
                        continue;
                    }
                }
                // Add the method to the matching methods list.
                matchingMethodsList.add(method);
            }
        }

        // DIFFBLUE DEEPTESTUTILS
        // Remove duplicates that only differ in return value
        if (allMethodsHaveTheSameParameters(matchingMethodsList)) {
            matchingMethodsList = removeDuplicatesBasedOnReturnType(type,
                    matchingMethodsList);
        }

        Method methodToMock = null;
        if (matchingMethodsList.size() > 0) {
            if (matchingMethodsList.size() == 1) {
                // We've found a unique method match.
                methodToMock = matchingMethodsList.get(0);
            } else if (parameterTypes == null || parameterTypes.length == 0) {
                /*
                 * If we've found several matches and we've supplied no
                 * parameter types, go through the list of found methods and see
                 * if we have a method with no parameters. In that case return
                 * that method.
                 */
                for (Method method : matchingMethodsList) {
                    if (method.getParameterTypes().length == 0) {
                        methodToMock = method;
                        break;
                    }
                }

                if (methodToMock == null) {
                    throwExceptionWhenMultipleMethodMatchesFound(
                            "argument parameter types",
                            matchingMethodsList.toArray(
                                    new Method[matchingMethodsList.size()]));
                }
            } else {
                // We've found several matching methods.
                throwExceptionWhenMultipleMethodMatchesFound(
                        "argument parameter types",
                        matchingMethodsList.toArray(
                                new Method[matchingMethodsList.size()]));
            }
        }

        return methodToMock;
    }
}
