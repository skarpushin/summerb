package org.summerb.validation.jakarta.test_data2;

import org.summerb.validation.jakarta.AnnotationProcessor;

import javax.validation.constraints.Max;

public interface AbstractInterfaceGeneric<T> extends AnnotationProcessor<Max> {

  void method(T t);
}