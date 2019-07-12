1.10.0
======
* [TG-8457] Upgrade javassist to 3.25.0-GA

1.9.0
=====
* [TG-5859] Always use Objenesis to instantiate objects, never call real constructors
* [TG-5895] Fix wrapping of errors within `InvocationTargetException`s in `Reflector.getInstance`

1.8.1
=====
* Work around Maven Surefire plugin bug

1.8.0
=====
* Downgrade Powermock from 1.6.6 to 1.6.5
* Introduce DTUMemberMatcher class to deal with PowerMockito issues.

1.7.1
=====
* Update version number in pom file properly.

1.7.0
=====
* [TG-4312] Expose private overload of `setField`

1.6.0
=====
* [TG-3765] Fix check in toThrowableClass

1.5.0
=====
* Add a method to Reflector to set a static field of a class

1.4.0
=====
* Wrap exceptions from Deeptestutils in a runtime exception

1.3.1
======
* Fix Java 1.6 deployment

1.3.0
======
* [TG-3881] Allow `InvocationTargetException` in `Reflector.getInstance`
* Use HTTP maven central mirror for openJDK6

1.2.0
=====
* [TG-3495] Support Java 6 and Java 7

1.1.0
=====
* Update license to BSD-3 Clause


1.0.0
=====
 * Initial release of deeptest-utils
 * [TG-1937] Expose private method to facilitate accessing hidden fields
