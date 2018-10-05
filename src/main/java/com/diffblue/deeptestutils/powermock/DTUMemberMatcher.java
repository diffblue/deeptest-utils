// Copyright 2016-2018 Diffblue limited. All rights reserved.

package com.diffblue.deeptestutils.powermock;

import java.lang.reflect.Method;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.reflect.internal.WhiteboxImpl;

/**
 * <code>DTUMemberMatcher</code> extends the MemberMatcher class in PowerMock
 * and fixes the issues below.
 * - TG-5254
 *
 * @author <a href="http://diffblue.com">Diffblue</a>
 */
public class DTUMemberMatcher extends MemberMatcher {
    /**
     * Copied from org.powermock.api.support.membermodification.MemberMatcher
     * and calling DTUWhiteboxImpl instead of
     * org.powermock.reflect.internal.WhiteboxImpl.
     *
     * Get a method when it cannot be determined by methodName or parameter
     * types only.
     * <p>
     * The method will first try to look for a declared method in the same
     * class. If the method is not declared in this class it will look for the
     * method in the super class. This will continue throughout the whole class
     * hierarchy. If the method is not found an {@link IllegalArgumentException}
     * is thrown.
     *
     * @param declaringClass
     *            The declaringClass of the class where the method is located.
     * @param methodName
     *            The method names.
     * @param parameterTypes
     *            All parameter types of the method (may be <code>null</code>).
     * @return A <code>java.lang.reflect.Method</code>.
     * @throws MethodNotFoundException
     *            If a method cannot be found in the hierarchy.
     */
    public static Method method(final Class<?> declaringClass,
                                final String methodName,
                                final Class<?>... parameterTypes) {
        final Method method = DTUWhiteboxImpl.findMethod(declaringClass,
                methodName, parameterTypes);
        WhiteboxImpl.throwExceptionIfMethodWasNotFound(declaringClass,
                methodName, method, (Object[]) parameterTypes);
        return method;
    }
}
