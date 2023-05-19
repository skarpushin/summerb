package org.summerb.validation.jakarta.test_data2;

import javax.validation.constraints.Max;

import org.summerb.validation.jakarta.AnnotationProcessor;

public interface AbstractInterfaceGeneric<T> extends AnnotationProcessor<Max> {

  void method(T t);
}
