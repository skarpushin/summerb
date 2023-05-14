package org.summerb.methodCapturers;

import java.lang.reflect.Method;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

public class MethodCapturingInterceptor {

  @RuntimeType
  public static Object intercept(@This MethodCapturer capturer, @Origin Method method) {
    capturer.set__Method(method);
    return null;

    // NOTE: You'd think that we'd have to return different default value for different return types
    // (as commented-out below) but it doesn't matter at all. We can just return null regardless of
    // return type and code will work just fine. See MethodCapturingInterceptorTest -- it prooves
    // it.

    //    Class<?> returnType = method.getReturnType();
    //    if (!returnType.isPrimitive()) {
    //      return null;
    //    }
    //
    //    if (boolean.class.equals(returnType)) {
    //      return false;
    //    } else if (byte.class.equals(returnType)) {
    //      return (byte) 0;
    //    } else if (char.class.equals(returnType)) {
    //      return (char) 0;
    //    } else if (short.class.equals(returnType)) {
    //      return (short) 0;
    //    } else if (int.class.equals(returnType)) {
    //      return (int) 0;
    //    } else if (long.class.equals(returnType)) {
    //      return 0L;
    //    } else if (float.class.equals(returnType)) {
    //      return 0f;
    //    } else if (double.class.equals(returnType)) {
    //      return 0d;
    //    } else {
    //      throw new IllegalStateException("Type not supported " + returnType);
    //    }
  }
}
