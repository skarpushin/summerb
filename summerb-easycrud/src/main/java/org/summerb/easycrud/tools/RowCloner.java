package org.summerb.easycrud.tools;

/**
 * Interface that clones Rows/DTOs.
 *
 * <p>NOTE: Not using generic typing because I want to utilize autowiring for all instances of
 * {@link org.summerb.easycrud.impl.EasyCrudServiceImpl}
 */
public interface RowCloner {
  Object clone(Object row);
}
