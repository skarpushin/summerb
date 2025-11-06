package org.summerb.easycrud.join_query.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.summerb.easycrud.join_query.impl.JoinQueryImpl;
import org.summerb.easycrud.row.HasId;

/**
 * Use this annotation on a Row's field that represents a foreign key to other Row. This
 * meta-information can later be used by {@link JoinQueryImpl} to automatically identify join
 * conditions
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferringTo {
  Class<? extends HasId<?>> value();
}
