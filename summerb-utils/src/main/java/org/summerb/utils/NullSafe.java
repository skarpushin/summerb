package org.summerb.utils;

import java.util.function.Function;

/**
 * A parser that helps to get the values from objects with many nesting levels avoiding NPE on each
 * level
 */
public class NullSafe {

  /**
   * Gets the U value applying function firstFunction from T object, it used for one nesting level
   * objects
   *
   * @param t the object that you need to get a value form, a root of the tree
   * @param firstFunction a one argument function (often a lambda) that extracts the U value from T
   * @param <T> an object type you're going to parse (root nesting level)
   * @param <U> an object type you expect to get (1st nesting level object)
   * @return returns U object if neither T t, nor U object is null. Otherwise, returns null.
   */
  public static <T, U> U get(T t, Function<T, U> firstFunction) {
    return t != null ? (firstFunction != null ? firstFunction.apply(t) : null) : null;
  }

  /**
   * Gets the R object applying the functions firstFunction and secondFunction one by one to T t
   * argument
   *
   * @param t the object that you need to get a value form, a root of the tree
   * @param firstFunction a first one argument function (often a lambda) that extracts the U value
   *     from T
   * @param secondFunction a second one argument function (often a lambda) that extracts the R value
   *     from U
   * @param <T> an object type you're going to parse (root nesting level), a root of the tree
   * @param <U> a first nesting level object (1st nesting level object), a kind of "brach of the
   *     tree"
   * @param <R> a second nesting level object (2nd nesting level object), a kind of "tree leaf"
   * @return returns R object if neither of T, U and R is null. Otherwise, returns null.
   */
  public static <T, U, R> R get(T t, Function<T, U> firstFunction, Function<U, R> secondFunction) {
    U u = get(t, firstFunction);
    return get(u, secondFunction);
  }

  /**
   * Gets the R object applying the functions firstFunction, secondFunction, thirdFunction one by
   * one to T t argument
   *
   * @param t the object that you need to get a value form, a root of the tree
   * @param firstFunction a first one argument function (often a lambda) that extracts the U value
   *     from T
   * @param secondFunction a second one argument function (often a lambda) that extracts the R value
   *     from U
   * @param thirdFunction a third one argument function (often a lambda) that extracts the S value
   *     from R
   * @param <T> an object type you're going to parse (root nesting level), a root of the tree
   * @param <U> a first nesting level object (1st nesting level object), a kind of "brach of the
   *     tree"
   * @param <R> a second nesting level object (2nd nesting level object), a kind of "next level
   *     brach of the tree"
   * @param <S> a third nesting level object (3rd nesting level object), a kind of "tree leaf"
   * @return returns R object if neither of T, U, R and S is null. Otherwise, returns null.
   */
  public static <T, U, R, S> S get(
      T t,
      Function<T, U> firstFunction,
      Function<U, R> secondFunction,
      Function<R, S> thirdFunction) {
    R r = get(t, firstFunction, secondFunction);
    return get(r, thirdFunction);
  }

  public static <T, U, R, S, V> V get(
      T t,
      Function<T, U> firstFunction,
      Function<U, R> secondFunction,
      Function<R, S> thirdFunction,
      Function<S, V> fourthFunction) {
    S r = get(t, firstFunction, secondFunction, thirdFunction);
    return get(r, fourthFunction);
  }
}
