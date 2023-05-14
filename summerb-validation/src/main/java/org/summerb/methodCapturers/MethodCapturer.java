package org.summerb.methodCapturers;

import java.lang.reflect.Method;

/**
 * A helper interface which implementation will be created as a proxy (mix-in) to original POJO
 * (ROW) class. Instance is supposed to be obtained via {@link MethodCapturerProxyClassFactory}
 *
 * @author Sergey Karpushin
 */
public interface MethodCapturer {
  Method get__Method();

  void set__Method(Method methodName);
}
