package org.summerb.methodCapturers;

/**
 * This factory is responsible to return PropertyName obtainers.
 *
 * <p>Impl is supposed to be cached to overcome reflection cost
 *
 * @author Sergey Karpushin
 */
public interface PropertyNameObtainerFactory {

  <T> PropertyNameObtainer<T> getObtainer(Class<T> beanClass);
}
